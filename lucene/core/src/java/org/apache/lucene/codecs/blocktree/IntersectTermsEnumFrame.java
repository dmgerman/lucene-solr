begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.blocktree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktree
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|automaton
operator|.
name|Transition
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

begin_comment
comment|// TODO: can we share this with the frame in STE?
end_comment

begin_class
DECL|class|IntersectTermsEnumFrame
specifier|final
class|class
name|IntersectTermsEnumFrame
block|{
DECL|field|ord
specifier|final
name|int
name|ord
decl_stmt|;
DECL|field|fp
name|long
name|fp
decl_stmt|;
DECL|field|fpOrig
name|long
name|fpOrig
decl_stmt|;
DECL|field|fpEnd
name|long
name|fpEnd
decl_stmt|;
DECL|field|lastSubFP
name|long
name|lastSubFP
decl_stmt|;
comment|// State in automaton
DECL|field|state
name|int
name|state
decl_stmt|;
DECL|field|metaDataUpto
name|int
name|metaDataUpto
decl_stmt|;
DECL|field|suffixBytes
name|byte
index|[]
name|suffixBytes
init|=
operator|new
name|byte
index|[
literal|128
index|]
decl_stmt|;
DECL|field|suffixesReader
specifier|final
name|ByteArrayDataInput
name|suffixesReader
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
DECL|field|statBytes
name|byte
index|[]
name|statBytes
init|=
operator|new
name|byte
index|[
literal|64
index|]
decl_stmt|;
DECL|field|statsReader
specifier|final
name|ByteArrayDataInput
name|statsReader
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
DECL|field|floorData
name|byte
index|[]
name|floorData
init|=
operator|new
name|byte
index|[
literal|32
index|]
decl_stmt|;
DECL|field|floorDataReader
specifier|final
name|ByteArrayDataInput
name|floorDataReader
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
comment|// Length of prefix shared by all terms in this block
DECL|field|prefix
name|int
name|prefix
decl_stmt|;
comment|// Number of entries (term or sub-block) in this block
DECL|field|entCount
name|int
name|entCount
decl_stmt|;
comment|// Which term we will next read
DECL|field|nextEnt
name|int
name|nextEnt
decl_stmt|;
comment|// True if this block is either not a floor block,
comment|// or, it's the last sub-block of a floor block
DECL|field|isLastInFloor
name|boolean
name|isLastInFloor
decl_stmt|;
comment|// True if all entries are terms
DECL|field|isLeafBlock
name|boolean
name|isLeafBlock
decl_stmt|;
DECL|field|numFollowFloorBlocks
name|int
name|numFollowFloorBlocks
decl_stmt|;
DECL|field|nextFloorLabel
name|int
name|nextFloorLabel
decl_stmt|;
DECL|field|transition
name|Transition
name|transition
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
DECL|field|curTransitionMax
name|int
name|curTransitionMax
decl_stmt|;
DECL|field|transitionIndex
name|int
name|transitionIndex
decl_stmt|;
DECL|field|transitionCount
name|int
name|transitionCount
decl_stmt|;
DECL|field|arc
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|arc
decl_stmt|;
DECL|field|termState
specifier|final
name|BlockTermState
name|termState
decl_stmt|;
comment|// metadata buffer, holding monotonic values
DECL|field|longs
specifier|public
name|long
index|[]
name|longs
decl_stmt|;
comment|// metadata buffer, holding general values
DECL|field|bytes
specifier|public
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|bytesReader
name|ByteArrayDataInput
name|bytesReader
decl_stmt|;
comment|// Cumulative output so far
DECL|field|outputPrefix
name|BytesRef
name|outputPrefix
decl_stmt|;
DECL|field|startBytePos
name|int
name|startBytePos
decl_stmt|;
DECL|field|suffix
name|int
name|suffix
decl_stmt|;
DECL|field|ite
specifier|private
specifier|final
name|IntersectTermsEnum
name|ite
decl_stmt|;
DECL|method|IntersectTermsEnumFrame
specifier|public
name|IntersectTermsEnumFrame
parameter_list|(
name|IntersectTermsEnum
name|ite
parameter_list|,
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|ite
operator|=
name|ite
expr_stmt|;
name|this
operator|.
name|ord
operator|=
name|ord
expr_stmt|;
name|this
operator|.
name|termState
operator|=
name|ite
operator|.
name|fr
operator|.
name|parent
operator|.
name|postingsReader
operator|.
name|newTermState
argument_list|()
expr_stmt|;
name|this
operator|.
name|termState
operator|.
name|totalTermFreq
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|longs
operator|=
operator|new
name|long
index|[
name|ite
operator|.
name|fr
operator|.
name|longsSize
index|]
expr_stmt|;
block|}
DECL|method|loadNextFloorBlock
name|void
name|loadNextFloorBlock
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|numFollowFloorBlocks
operator|>
literal|0
assert|;
comment|//if (DEBUG) System.out.println("    loadNextFoorBlock trans=" + transitions[transitionIndex]);
do|do
block|{
name|fp
operator|=
name|fpOrig
operator|+
operator|(
name|floorDataReader
operator|.
name|readVLong
argument_list|()
operator|>>>
literal|1
operator|)
expr_stmt|;
name|numFollowFloorBlocks
operator|--
expr_stmt|;
comment|// if (DEBUG) System.out.println("    skip floor block2!  nextFloorLabel=" + (char) nextFloorLabel + " vs target=" + (char) transitions[transitionIndex].getMin() + " newFP=" + fp + " numFollowFloorBlocks=" + numFollowFloorBlocks);
if|if
condition|(
name|numFollowFloorBlocks
operator|!=
literal|0
condition|)
block|{
name|nextFloorLabel
operator|=
name|floorDataReader
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
expr_stmt|;
block|}
else|else
block|{
name|nextFloorLabel
operator|=
literal|256
expr_stmt|;
block|}
comment|// if (DEBUG) System.out.println("    nextFloorLabel=" + (char) nextFloorLabel);
block|}
do|while
condition|(
name|numFollowFloorBlocks
operator|!=
literal|0
operator|&&
name|nextFloorLabel
operator|<=
name|transition
operator|.
name|min
condition|)
do|;
name|load
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|setState
specifier|public
name|void
name|setState
parameter_list|(
name|int
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|transitionIndex
operator|=
literal|0
expr_stmt|;
name|transitionCount
operator|=
name|ite
operator|.
name|compiledAutomaton
operator|.
name|automaton
operator|.
name|getNumTransitions
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|transitionCount
operator|!=
literal|0
condition|)
block|{
name|ite
operator|.
name|compiledAutomaton
operator|.
name|automaton
operator|.
name|initTransition
argument_list|(
name|state
argument_list|,
name|transition
argument_list|)
expr_stmt|;
name|ite
operator|.
name|compiledAutomaton
operator|.
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|transition
argument_list|)
expr_stmt|;
name|curTransitionMax
operator|=
name|transition
operator|.
name|max
expr_stmt|;
block|}
else|else
block|{
name|curTransitionMax
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|load
name|void
name|load
parameter_list|(
name|BytesRef
name|frameIndexData
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("    load fp=" + fp + " fpOrig=" + fpOrig + " frameIndexData=" + frameIndexData + " trans=" + (transitions.length != 0 ? transitions[0] : "n/a" + " state=" + state));
if|if
condition|(
name|frameIndexData
operator|!=
literal|null
operator|&&
name|transitionCount
operator|!=
literal|0
condition|)
block|{
comment|// Floor frame
if|if
condition|(
name|floorData
operator|.
name|length
operator|<
name|frameIndexData
operator|.
name|length
condition|)
block|{
name|this
operator|.
name|floorData
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|frameIndexData
operator|.
name|length
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|frameIndexData
operator|.
name|bytes
argument_list|,
name|frameIndexData
operator|.
name|offset
argument_list|,
name|floorData
argument_list|,
literal|0
argument_list|,
name|frameIndexData
operator|.
name|length
argument_list|)
expr_stmt|;
name|floorDataReader
operator|.
name|reset
argument_list|(
name|floorData
argument_list|,
literal|0
argument_list|,
name|frameIndexData
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Skip first long -- has redundant fp, hasTerms
comment|// flag, isFloor flag
specifier|final
name|long
name|code
init|=
name|floorDataReader
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
name|BlockTreeTermsReader
operator|.
name|OUTPUT_FLAG_IS_FLOOR
operator|)
operator|!=
literal|0
condition|)
block|{
name|numFollowFloorBlocks
operator|=
name|floorDataReader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|nextFloorLabel
operator|=
name|floorDataReader
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
expr_stmt|;
comment|// if (DEBUG) System.out.println("    numFollowFloorBlocks=" + numFollowFloorBlocks + " nextFloorLabel=" + nextFloorLabel);
comment|// If current state is accept, we must process
comment|// first block in case it has empty suffix:
if|if
condition|(
operator|!
name|ite
operator|.
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
comment|// Maybe skip floor blocks:
assert|assert
name|transitionIndex
operator|==
literal|0
operator|:
literal|"transitionIndex="
operator|+
name|transitionIndex
assert|;
while|while
condition|(
name|numFollowFloorBlocks
operator|!=
literal|0
operator|&&
name|nextFloorLabel
operator|<=
name|transition
operator|.
name|min
condition|)
block|{
name|fp
operator|=
name|fpOrig
operator|+
operator|(
name|floorDataReader
operator|.
name|readVLong
argument_list|()
operator|>>>
literal|1
operator|)
expr_stmt|;
name|numFollowFloorBlocks
operator|--
expr_stmt|;
comment|// if (DEBUG) System.out.println("    skip floor block!  nextFloorLabel=" + (char) nextFloorLabel + " vs target=" + (char) transitions[0].getMin() + " newFP=" + fp + " numFollowFloorBlocks=" + numFollowFloorBlocks);
if|if
condition|(
name|numFollowFloorBlocks
operator|!=
literal|0
condition|)
block|{
name|nextFloorLabel
operator|=
name|floorDataReader
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
expr_stmt|;
block|}
else|else
block|{
name|nextFloorLabel
operator|=
literal|256
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|ite
operator|.
name|in
operator|.
name|seek
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|int
name|code
init|=
name|ite
operator|.
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|entCount
operator|=
name|code
operator|>>>
literal|1
expr_stmt|;
assert|assert
name|entCount
operator|>
literal|0
assert|;
name|isLastInFloor
operator|=
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
expr_stmt|;
comment|// term suffixes:
name|code
operator|=
name|ite
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|isLeafBlock
operator|=
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
expr_stmt|;
name|int
name|numBytes
init|=
name|code
operator|>>>
literal|1
decl_stmt|;
comment|// if (DEBUG) System.out.println("      entCount=" + entCount + " lastInFloor?=" + isLastInFloor + " leafBlock?=" + isLeafBlock + " numSuffixBytes=" + numBytes);
if|if
condition|(
name|suffixBytes
operator|.
name|length
operator|<
name|numBytes
condition|)
block|{
name|suffixBytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numBytes
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|ite
operator|.
name|in
operator|.
name|readBytes
argument_list|(
name|suffixBytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|suffixesReader
operator|.
name|reset
argument_list|(
name|suffixBytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
comment|// stats
name|numBytes
operator|=
name|ite
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|statBytes
operator|.
name|length
operator|<
name|numBytes
condition|)
block|{
name|statBytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numBytes
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|ite
operator|.
name|in
operator|.
name|readBytes
argument_list|(
name|statBytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|statsReader
operator|.
name|reset
argument_list|(
name|statBytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|metaDataUpto
operator|=
literal|0
expr_stmt|;
name|termState
operator|.
name|termBlockOrd
operator|=
literal|0
expr_stmt|;
name|nextEnt
operator|=
literal|0
expr_stmt|;
comment|// metadata
name|numBytes
operator|=
name|ite
operator|.
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numBytes
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
name|bytesReader
operator|=
operator|new
name|ByteArrayDataInput
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|length
operator|<
name|numBytes
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numBytes
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|ite
operator|.
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|bytesReader
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isLastInFloor
condition|)
block|{
comment|// Sub-blocks of a single floor block are always
comment|// written one after another -- tail recurse:
name|fpEnd
operator|=
name|ite
operator|.
name|in
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO: maybe add scanToLabel; should give perf boost
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
block|{
return|return
name|isLeafBlock
condition|?
name|nextLeaf
argument_list|()
else|:
name|nextNonLeaf
argument_list|()
return|;
block|}
comment|// Decodes next entry; returns true if it's a sub-block
DECL|method|nextLeaf
specifier|public
name|boolean
name|nextLeaf
parameter_list|()
block|{
comment|//if (DEBUG) System.out.println("  frame.next ord=" + ord + " nextEnt=" + nextEnt + " entCount=" + entCount);
assert|assert
name|nextEnt
operator|!=
operator|-
literal|1
operator|&&
name|nextEnt
operator|<
name|entCount
operator|:
literal|"nextEnt="
operator|+
name|nextEnt
operator|+
literal|" entCount="
operator|+
name|entCount
operator|+
literal|" fp="
operator|+
name|fp
assert|;
name|nextEnt
operator|++
expr_stmt|;
name|suffix
operator|=
name|suffixesReader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|startBytePos
operator|=
name|suffixesReader
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|suffixesReader
operator|.
name|skipBytes
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|nextNonLeaf
specifier|public
name|boolean
name|nextNonLeaf
parameter_list|()
block|{
comment|//if (DEBUG) System.out.println("  frame.next ord=" + ord + " nextEnt=" + nextEnt + " entCount=" + entCount);
assert|assert
name|nextEnt
operator|!=
operator|-
literal|1
operator|&&
name|nextEnt
operator|<
name|entCount
operator|:
literal|"nextEnt="
operator|+
name|nextEnt
operator|+
literal|" entCount="
operator|+
name|entCount
operator|+
literal|" fp="
operator|+
name|fp
assert|;
name|nextEnt
operator|++
expr_stmt|;
specifier|final
name|int
name|code
init|=
name|suffixesReader
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|suffix
operator|=
name|code
operator|>>>
literal|1
expr_stmt|;
name|startBytePos
operator|=
name|suffixesReader
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|suffixesReader
operator|.
name|skipBytes
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
comment|// A normal term
name|termState
operator|.
name|termBlockOrd
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// A sub-block; make sub-FP absolute:
name|lastSubFP
operator|=
name|fp
operator|-
name|suffixesReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|getTermBlockOrd
specifier|public
name|int
name|getTermBlockOrd
parameter_list|()
block|{
return|return
name|isLeafBlock
condition|?
name|nextEnt
else|:
name|termState
operator|.
name|termBlockOrd
return|;
block|}
DECL|method|decodeMetaData
specifier|public
name|void
name|decodeMetaData
parameter_list|()
throws|throws
name|IOException
block|{
comment|// lazily catch up on metadata decode:
specifier|final
name|int
name|limit
init|=
name|getTermBlockOrd
argument_list|()
decl_stmt|;
name|boolean
name|absolute
init|=
name|metaDataUpto
operator|==
literal|0
decl_stmt|;
assert|assert
name|limit
operator|>
literal|0
assert|;
comment|// TODO: better API would be "jump straight to term=N"???
while|while
condition|(
name|metaDataUpto
operator|<
name|limit
condition|)
block|{
comment|// TODO: we could make "tiers" of metadata, ie,
comment|// decode docFreq/totalTF but don't decode postings
comment|// metadata; this way caller could get
comment|// docFreq/totalTF w/o paying decode cost for
comment|// postings
comment|// TODO: if docFreq were bulk decoded we could
comment|// just skipN here:
comment|// stats
name|termState
operator|.
name|docFreq
operator|=
name|statsReader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("    dF=" + state.docFreq);
if|if
condition|(
name|ite
operator|.
name|fr
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|termState
operator|.
name|totalTermFreq
operator|=
name|termState
operator|.
name|docFreq
operator|+
name|statsReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("    totTF=" + state.totalTermFreq);
block|}
comment|// metadata
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ite
operator|.
name|fr
operator|.
name|longsSize
condition|;
name|i
operator|++
control|)
block|{
name|longs
index|[
name|i
index|]
operator|=
name|bytesReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
name|ite
operator|.
name|fr
operator|.
name|parent
operator|.
name|postingsReader
operator|.
name|decodeTerm
argument_list|(
name|longs
argument_list|,
name|bytesReader
argument_list|,
name|ite
operator|.
name|fr
operator|.
name|fieldInfo
argument_list|,
name|termState
argument_list|,
name|absolute
argument_list|)
expr_stmt|;
name|metaDataUpto
operator|++
expr_stmt|;
name|absolute
operator|=
literal|false
expr_stmt|;
block|}
name|termState
operator|.
name|termBlockOrd
operator|=
name|metaDataUpto
expr_stmt|;
block|}
block|}
end_class

end_unit

