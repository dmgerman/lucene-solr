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
name|index
operator|.
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|index
operator|.
name|TermState
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
name|Bits
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
name|CompiledAutomaton
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
name|RunAutomaton
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
name|Outputs
import|;
end_import

begin_comment
comment|// NOTE: cannot seek!
end_comment

begin_class
DECL|class|IntersectTermsEnum
specifier|final
class|class
name|IntersectTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|fstOutputs
specifier|final
specifier|static
name|Outputs
argument_list|<
name|BytesRef
argument_list|>
name|fstOutputs
init|=
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
DECL|field|stack
specifier|private
name|IntersectTermsEnumFrame
index|[]
name|stack
decl_stmt|;
DECL|field|arcs
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|private
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
index|[]
name|arcs
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
literal|5
index|]
decl_stmt|;
DECL|field|runAutomaton
specifier|final
name|RunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|compiledAutomaton
specifier|final
name|CompiledAutomaton
name|compiledAutomaton
decl_stmt|;
DECL|field|currentFrame
specifier|private
name|IntersectTermsEnumFrame
name|currentFrame
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|fstReader
specifier|private
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
decl_stmt|;
DECL|field|fr
specifier|final
name|FieldReader
name|fr
decl_stmt|;
DECL|field|savedStartTerm
specifier|private
name|BytesRef
name|savedStartTerm
decl_stmt|;
comment|// TODO: in some cases we can filter by length?  eg
comment|// regexp foo*bar must be at least length 6 bytes
DECL|method|IntersectTermsEnum
specifier|public
name|IntersectTermsEnum
parameter_list|(
name|FieldReader
name|fr
parameter_list|,
name|CompiledAutomaton
name|compiled
parameter_list|,
name|BytesRef
name|startTerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("\nintEnum.init seg=" + segment + " commonSuffix=" + brToString(compiled.commonSuffixRef));
comment|// }
name|this
operator|.
name|fr
operator|=
name|fr
expr_stmt|;
name|runAutomaton
operator|=
name|compiled
operator|.
name|runAutomaton
expr_stmt|;
name|compiledAutomaton
operator|=
name|compiled
expr_stmt|;
name|in
operator|=
name|fr
operator|.
name|parent
operator|.
name|termsIn
operator|.
name|clone
argument_list|()
expr_stmt|;
name|stack
operator|=
operator|new
name|IntersectTermsEnumFrame
index|[
literal|5
index|]
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|stack
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|stack
index|[
name|idx
index|]
operator|=
operator|new
name|IntersectTermsEnumFrame
argument_list|(
name|this
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|arcIdx
init|=
literal|0
init|;
name|arcIdx
operator|<
name|arcs
operator|.
name|length
condition|;
name|arcIdx
operator|++
control|)
block|{
name|arcs
index|[
name|arcIdx
index|]
operator|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fr
operator|.
name|index
operator|==
literal|null
condition|)
block|{
name|fstReader
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|fstReader
operator|=
name|fr
operator|.
name|index
operator|.
name|getBytesReader
argument_list|()
expr_stmt|;
block|}
comment|// TODO: if the automaton is "smallish" we really
comment|// should use the terms index to seek at least to
comment|// the initial term and likely to subsequent terms
comment|// (or, maybe just fallback to ATE for such cases).
comment|// Else the seek cost of loading the frames will be
comment|// too costly.
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|arc
init|=
name|fr
operator|.
name|index
operator|.
name|getFirstArc
argument_list|(
name|arcs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// Empty string prefix must have an output in the index!
assert|assert
name|arc
operator|.
name|isFinal
argument_list|()
assert|;
comment|// Special pushFrame since it's the first one:
specifier|final
name|IntersectTermsEnumFrame
name|f
init|=
name|stack
index|[
literal|0
index|]
decl_stmt|;
name|f
operator|.
name|fp
operator|=
name|f
operator|.
name|fpOrig
operator|=
name|fr
operator|.
name|rootBlockFP
expr_stmt|;
name|f
operator|.
name|prefix
operator|=
literal|0
expr_stmt|;
name|f
operator|.
name|setState
argument_list|(
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|arc
operator|=
name|arc
expr_stmt|;
name|f
operator|.
name|outputPrefix
operator|=
name|arc
operator|.
name|output
expr_stmt|;
name|f
operator|.
name|load
argument_list|(
name|fr
operator|.
name|rootCode
argument_list|)
expr_stmt|;
comment|// for assert:
assert|assert
name|setSavedStartTerm
argument_list|(
name|startTerm
argument_list|)
assert|;
name|currentFrame
operator|=
name|f
expr_stmt|;
if|if
condition|(
name|startTerm
operator|!=
literal|null
condition|)
block|{
name|seekToStartTerm
argument_list|(
name|startTerm
argument_list|)
expr_stmt|;
block|}
block|}
comment|// only for assert:
DECL|method|setSavedStartTerm
specifier|private
name|boolean
name|setSavedStartTerm
parameter_list|(
name|BytesRef
name|startTerm
parameter_list|)
block|{
name|savedStartTerm
operator|=
name|startTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|startTerm
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
name|currentFrame
operator|.
name|decodeMetaData
argument_list|()
expr_stmt|;
return|return
name|currentFrame
operator|.
name|termState
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getFrame
specifier|private
name|IntersectTermsEnumFrame
name|getFrame
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ord
operator|>=
name|stack
operator|.
name|length
condition|)
block|{
specifier|final
name|IntersectTermsEnumFrame
index|[]
name|next
init|=
operator|new
name|IntersectTermsEnumFrame
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|ord
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|stack
argument_list|,
literal|0
argument_list|,
name|next
argument_list|,
literal|0
argument_list|,
name|stack
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|stackOrd
init|=
name|stack
operator|.
name|length
init|;
name|stackOrd
operator|<
name|next
operator|.
name|length
condition|;
name|stackOrd
operator|++
control|)
block|{
name|next
index|[
name|stackOrd
index|]
operator|=
operator|new
name|IntersectTermsEnumFrame
argument_list|(
name|this
argument_list|,
name|stackOrd
argument_list|)
expr_stmt|;
block|}
name|stack
operator|=
name|next
expr_stmt|;
block|}
assert|assert
name|stack
index|[
name|ord
index|]
operator|.
name|ord
operator|==
name|ord
assert|;
return|return
name|stack
index|[
name|ord
index|]
return|;
block|}
DECL|method|getArc
specifier|private
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|getArc
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
if|if
condition|(
name|ord
operator|>=
name|arcs
operator|.
name|length
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
index|[]
name|next
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|ord
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arcs
argument_list|,
literal|0
argument_list|,
name|next
argument_list|,
literal|0
argument_list|,
name|arcs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|arcOrd
init|=
name|arcs
operator|.
name|length
init|;
name|arcOrd
operator|<
name|next
operator|.
name|length
condition|;
name|arcOrd
operator|++
control|)
block|{
name|next
index|[
name|arcOrd
index|]
operator|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|arcs
operator|=
name|next
expr_stmt|;
block|}
return|return
name|arcs
index|[
name|ord
index|]
return|;
block|}
DECL|method|pushFrame
specifier|private
name|IntersectTermsEnumFrame
name|pushFrame
parameter_list|(
name|int
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IntersectTermsEnumFrame
name|f
init|=
name|getFrame
argument_list|(
name|currentFrame
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|+
name|currentFrame
operator|.
name|ord
argument_list|)
decl_stmt|;
name|f
operator|.
name|fp
operator|=
name|f
operator|.
name|fpOrig
operator|=
name|currentFrame
operator|.
name|lastSubFP
expr_stmt|;
name|f
operator|.
name|prefix
operator|=
name|currentFrame
operator|.
name|prefix
operator|+
name|currentFrame
operator|.
name|suffix
expr_stmt|;
comment|// if (DEBUG) System.out.println("    pushFrame state=" + state + " prefix=" + f.prefix);
name|f
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|// Walk the arc through the index -- we only
comment|// "bother" with this so we can get the floor data
comment|// from the index and skip floor blocks when
comment|// possible:
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|arc
init|=
name|currentFrame
operator|.
name|arc
decl_stmt|;
name|int
name|idx
init|=
name|currentFrame
operator|.
name|prefix
decl_stmt|;
assert|assert
name|currentFrame
operator|.
name|suffix
operator|>
literal|0
assert|;
name|BytesRef
name|output
init|=
name|currentFrame
operator|.
name|outputPrefix
decl_stmt|;
while|while
condition|(
name|idx
operator|<
name|f
operator|.
name|prefix
condition|)
block|{
specifier|final
name|int
name|target
init|=
name|term
operator|.
name|bytes
index|[
name|idx
index|]
operator|&
literal|0xff
decl_stmt|;
comment|// TODO: we could be more efficient for the next()
comment|// case by using current arc as starting point,
comment|// passed to findTargetArc
name|arc
operator|=
name|fr
operator|.
name|index
operator|.
name|findTargetArc
argument_list|(
name|target
argument_list|,
name|arc
argument_list|,
name|getArc
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
assert|assert
name|arc
operator|!=
literal|null
assert|;
name|output
operator|=
name|fstOutputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
name|f
operator|.
name|arc
operator|=
name|arc
expr_stmt|;
name|f
operator|.
name|outputPrefix
operator|=
name|output
expr_stmt|;
assert|assert
name|arc
operator|.
name|isFinal
argument_list|()
assert|;
name|f
operator|.
name|load
argument_list|(
name|fstOutputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|nextFinalOutput
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
comment|//if (DEBUG) System.out.println("BTIR.docFreq");
name|currentFrame
operator|.
name|decodeMetaData
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("  return " + currentFrame.termState.docFreq);
return|return
name|currentFrame
operator|.
name|termState
operator|.
name|docFreq
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|currentFrame
operator|.
name|decodeMetaData
argument_list|()
expr_stmt|;
return|return
name|currentFrame
operator|.
name|termState
operator|.
name|totalTermFreq
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|currentFrame
operator|.
name|decodeMetaData
argument_list|()
expr_stmt|;
return|return
name|fr
operator|.
name|parent
operator|.
name|postingsReader
operator|.
name|docs
argument_list|(
name|fr
operator|.
name|fieldInfo
argument_list|,
name|currentFrame
operator|.
name|termState
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fr
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Positions were not indexed:
return|return
literal|null
return|;
block|}
name|currentFrame
operator|.
name|decodeMetaData
argument_list|()
expr_stmt|;
return|return
name|fr
operator|.
name|parent
operator|.
name|postingsReader
operator|.
name|docsAndPositions
argument_list|(
name|fr
operator|.
name|fieldInfo
argument_list|,
name|currentFrame
operator|.
name|termState
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
DECL|method|getState
specifier|private
name|int
name|getState
parameter_list|()
block|{
name|int
name|state
init|=
name|currentFrame
operator|.
name|state
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|currentFrame
operator|.
name|suffix
condition|;
name|idx
operator|++
control|)
block|{
name|state
operator|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|state
argument_list|,
name|currentFrame
operator|.
name|suffixBytes
index|[
name|currentFrame
operator|.
name|startBytePos
operator|+
name|idx
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
assert|assert
name|state
operator|!=
operator|-
literal|1
assert|;
block|}
return|return
name|state
return|;
block|}
comment|// NOTE: specialized to only doing the first-time
comment|// seek, but we could generalize it to allow
comment|// arbitrary seekExact/Ceil.  Note that this is a
comment|// seekFloor!
DECL|method|seekToStartTerm
specifier|private
name|void
name|seekToStartTerm
parameter_list|(
name|BytesRef
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) System.out.println("seek to startTerm=" + target.utf8ToString());
assert|assert
name|currentFrame
operator|.
name|ord
operator|==
literal|0
assert|;
if|if
condition|(
name|term
operator|.
name|length
operator|<
name|target
operator|.
name|length
condition|)
block|{
name|term
operator|.
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|target
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|arc
init|=
name|arcs
index|[
literal|0
index|]
decl_stmt|;
assert|assert
name|arc
operator|==
name|currentFrame
operator|.
name|arc
assert|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<=
name|target
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|savePos
init|=
name|currentFrame
operator|.
name|suffixesReader
operator|.
name|getPosition
argument_list|()
decl_stmt|;
specifier|final
name|int
name|saveStartBytePos
init|=
name|currentFrame
operator|.
name|startBytePos
decl_stmt|;
specifier|final
name|int
name|saveSuffix
init|=
name|currentFrame
operator|.
name|suffix
decl_stmt|;
specifier|final
name|long
name|saveLastSubFP
init|=
name|currentFrame
operator|.
name|lastSubFP
decl_stmt|;
specifier|final
name|int
name|saveTermBlockOrd
init|=
name|currentFrame
operator|.
name|termState
operator|.
name|termBlockOrd
decl_stmt|;
specifier|final
name|boolean
name|isSubBlock
init|=
name|currentFrame
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//if (DEBUG) System.out.println("    cycle ent=" + currentFrame.nextEnt + " (of " + currentFrame.entCount + ") prefix=" + currentFrame.prefix + " suffix=" + currentFrame.suffix + " isBlock=" + isSubBlock + " firstLabel=" + (currentFrame.suffix == 0 ? "" : (currentFrame.suffixBytes[currentFrame.startBytePos])&0xff));
name|term
operator|.
name|length
operator|=
name|currentFrame
operator|.
name|prefix
operator|+
name|currentFrame
operator|.
name|suffix
expr_stmt|;
if|if
condition|(
name|term
operator|.
name|bytes
operator|.
name|length
operator|<
name|term
operator|.
name|length
condition|)
block|{
name|term
operator|.
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|currentFrame
operator|.
name|suffixBytes
argument_list|,
name|currentFrame
operator|.
name|startBytePos
argument_list|,
name|term
operator|.
name|bytes
argument_list|,
name|currentFrame
operator|.
name|prefix
argument_list|,
name|currentFrame
operator|.
name|suffix
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubBlock
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|target
argument_list|,
name|term
argument_list|)
condition|)
block|{
comment|// Recurse
comment|//if (DEBUG) System.out.println("      recurse!");
name|currentFrame
operator|=
name|pushFrame
argument_list|(
name|getState
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
specifier|final
name|int
name|cmp
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|currentFrame
operator|.
name|nextEnt
operator|==
name|currentFrame
operator|.
name|entCount
condition|)
block|{
if|if
condition|(
operator|!
name|currentFrame
operator|.
name|isLastInFloor
condition|)
block|{
comment|//if (DEBUG) System.out.println("  load floorBlock");
name|currentFrame
operator|.
name|loadNextFloorBlock
argument_list|()
expr_stmt|;
continue|continue;
block|}
else|else
block|{
comment|//if (DEBUG) System.out.println("  return term=" + brToString(term));
return|return;
block|}
block|}
continue|continue;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
comment|//if (DEBUG) System.out.println("  return term=" + brToString(term));
return|return;
block|}
else|else
block|{
comment|// Fallback to prior entry: the semantics of
comment|// this method is that the first call to
comment|// next() will return the term after the
comment|// requested term
name|currentFrame
operator|.
name|nextEnt
operator|--
expr_stmt|;
name|currentFrame
operator|.
name|lastSubFP
operator|=
name|saveLastSubFP
expr_stmt|;
name|currentFrame
operator|.
name|startBytePos
operator|=
name|saveStartBytePos
expr_stmt|;
name|currentFrame
operator|.
name|suffix
operator|=
name|saveSuffix
expr_stmt|;
name|currentFrame
operator|.
name|suffixesReader
operator|.
name|setPosition
argument_list|(
name|savePos
argument_list|)
expr_stmt|;
name|currentFrame
operator|.
name|termState
operator|.
name|termBlockOrd
operator|=
name|saveTermBlockOrd
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|currentFrame
operator|.
name|suffixBytes
argument_list|,
name|currentFrame
operator|.
name|startBytePos
argument_list|,
name|term
operator|.
name|bytes
argument_list|,
name|currentFrame
operator|.
name|prefix
argument_list|,
name|currentFrame
operator|.
name|suffix
argument_list|)
expr_stmt|;
name|term
operator|.
name|length
operator|=
name|currentFrame
operator|.
name|prefix
operator|+
name|currentFrame
operator|.
name|suffix
expr_stmt|;
comment|// If the last entry was a block we don't
comment|// need to bother recursing and pushing to
comment|// the last term under it because the first
comment|// next() will simply skip the frame anyway
return|return;
block|}
block|}
block|}
block|}
assert|assert
literal|false
assert|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("\nintEnum.next seg=" + segment);
comment|//   System.out.println("  frame ord=" + currentFrame.ord + " prefix=" + brToString(new BytesRef(term.bytes, term.offset, currentFrame.prefix)) + " state=" + currentFrame.state + " lastInFloor?=" + currentFrame.isLastInFloor + " fp=" + currentFrame.fp + " trans=" + (currentFrame.transitions.length == 0 ? "n/a" : currentFrame.transitions[currentFrame.transitionIndex]) + " outputPrefix=" + currentFrame.outputPrefix);
comment|// }
name|nextTerm
label|:
while|while
condition|(
literal|true
condition|)
block|{
comment|// Pop finished frames
while|while
condition|(
name|currentFrame
operator|.
name|nextEnt
operator|==
name|currentFrame
operator|.
name|entCount
condition|)
block|{
if|if
condition|(
operator|!
name|currentFrame
operator|.
name|isLastInFloor
condition|)
block|{
comment|//if (DEBUG) System.out.println("    next-floor-block");
name|currentFrame
operator|.
name|loadNextFloorBlock
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("\n  frame ord=" + currentFrame.ord + " prefix=" + brToString(new BytesRef(term.bytes, term.offset, currentFrame.prefix)) + " state=" + currentFrame.state + " lastInFloor?=" + currentFrame.isLastInFloor + " fp=" + currentFrame.fp + " trans=" + (currentFrame.transitions.length == 0 ? "n/a" : currentFrame.transitions[currentFrame.transitionIndex]) + " outputPrefix=" + currentFrame.outputPrefix);
block|}
else|else
block|{
comment|//if (DEBUG) System.out.println("  pop frame");
if|if
condition|(
name|currentFrame
operator|.
name|ord
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|long
name|lastFP
init|=
name|currentFrame
operator|.
name|fpOrig
decl_stmt|;
name|currentFrame
operator|=
name|stack
index|[
name|currentFrame
operator|.
name|ord
operator|-
literal|1
index|]
expr_stmt|;
assert|assert
name|currentFrame
operator|.
name|lastSubFP
operator|==
name|lastFP
assert|;
comment|//if (DEBUG) System.out.println("\n  frame ord=" + currentFrame.ord + " prefix=" + brToString(new BytesRef(term.bytes, term.offset, currentFrame.prefix)) + " state=" + currentFrame.state + " lastInFloor?=" + currentFrame.isLastInFloor + " fp=" + currentFrame.fp + " trans=" + (currentFrame.transitions.length == 0 ? "n/a" : currentFrame.transitions[currentFrame.transitionIndex]) + " outputPrefix=" + currentFrame.outputPrefix);
block|}
block|}
specifier|final
name|boolean
name|isSubBlock
init|=
name|currentFrame
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// if (DEBUG) {
comment|//   final BytesRef suffixRef = new BytesRef();
comment|//   suffixRef.bytes = currentFrame.suffixBytes;
comment|//   suffixRef.offset = currentFrame.startBytePos;
comment|//   suffixRef.length = currentFrame.suffix;
comment|//   System.out.println("    " + (isSubBlock ? "sub-block" : "term") + " " + currentFrame.nextEnt + " (of " + currentFrame.entCount + ") suffix=" + brToString(suffixRef));
comment|// }
if|if
condition|(
name|currentFrame
operator|.
name|suffix
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|label
init|=
name|currentFrame
operator|.
name|suffixBytes
index|[
name|currentFrame
operator|.
name|startBytePos
index|]
operator|&
literal|0xff
decl_stmt|;
while|while
condition|(
name|label
operator|>
name|currentFrame
operator|.
name|curTransitionMax
condition|)
block|{
if|if
condition|(
name|currentFrame
operator|.
name|transitionIndex
operator|>=
name|currentFrame
operator|.
name|transitionCount
operator|-
literal|1
condition|)
block|{
comment|// Stop processing this frame -- no further
comment|// matches are possible because we've moved
comment|// beyond what the max transition will allow
comment|//if (DEBUG) System.out.println("      break: trans=" + (currentFrame.transitions.length == 0 ? "n/a" : currentFrame.transitions[currentFrame.transitionIndex]));
comment|// sneaky!  forces a pop above
name|currentFrame
operator|.
name|isLastInFloor
operator|=
literal|true
expr_stmt|;
name|currentFrame
operator|.
name|nextEnt
operator|=
name|currentFrame
operator|.
name|entCount
expr_stmt|;
continue|continue
name|nextTerm
continue|;
block|}
name|currentFrame
operator|.
name|transitionIndex
operator|++
expr_stmt|;
name|compiledAutomaton
operator|.
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|currentFrame
operator|.
name|transition
argument_list|)
expr_stmt|;
name|currentFrame
operator|.
name|curTransitionMax
operator|=
name|currentFrame
operator|.
name|transition
operator|.
name|max
expr_stmt|;
comment|//if (DEBUG) System.out.println("      next trans=" + currentFrame.transitions[currentFrame.transitionIndex]);
block|}
block|}
comment|// First test the common suffix, if set:
if|if
condition|(
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|!=
literal|null
operator|&&
operator|!
name|isSubBlock
condition|)
block|{
specifier|final
name|int
name|termLen
init|=
name|currentFrame
operator|.
name|prefix
operator|+
name|currentFrame
operator|.
name|suffix
decl_stmt|;
if|if
condition|(
name|termLen
operator|<
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|.
name|length
condition|)
block|{
comment|// No match
comment|// if (DEBUG) {
comment|//   System.out.println("      skip: common suffix length");
comment|// }
continue|continue
name|nextTerm
continue|;
block|}
specifier|final
name|byte
index|[]
name|suffixBytes
init|=
name|currentFrame
operator|.
name|suffixBytes
decl_stmt|;
specifier|final
name|byte
index|[]
name|commonSuffixBytes
init|=
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|lenInPrefix
init|=
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|.
name|length
operator|-
name|currentFrame
operator|.
name|suffix
decl_stmt|;
assert|assert
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|.
name|offset
operator|==
literal|0
assert|;
name|int
name|suffixBytesPos
decl_stmt|;
name|int
name|commonSuffixBytesPos
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|lenInPrefix
operator|>
literal|0
condition|)
block|{
comment|// A prefix of the common suffix overlaps with
comment|// the suffix of the block prefix so we first
comment|// test whether the prefix part matches:
specifier|final
name|byte
index|[]
name|termBytes
init|=
name|term
operator|.
name|bytes
decl_stmt|;
name|int
name|termBytesPos
init|=
name|currentFrame
operator|.
name|prefix
operator|-
name|lenInPrefix
decl_stmt|;
assert|assert
name|termBytesPos
operator|>=
literal|0
assert|;
specifier|final
name|int
name|termBytesPosEnd
init|=
name|currentFrame
operator|.
name|prefix
decl_stmt|;
while|while
condition|(
name|termBytesPos
operator|<
name|termBytesPosEnd
condition|)
block|{
if|if
condition|(
name|termBytes
index|[
name|termBytesPos
operator|++
index|]
operator|!=
name|commonSuffixBytes
index|[
name|commonSuffixBytesPos
operator|++
index|]
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("      skip: common suffix mismatch (in prefix)");
comment|// }
continue|continue
name|nextTerm
continue|;
block|}
block|}
name|suffixBytesPos
operator|=
name|currentFrame
operator|.
name|startBytePos
expr_stmt|;
block|}
else|else
block|{
name|suffixBytesPos
operator|=
name|currentFrame
operator|.
name|startBytePos
operator|+
name|currentFrame
operator|.
name|suffix
operator|-
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|.
name|length
expr_stmt|;
block|}
comment|// Test overlapping suffix part:
specifier|final
name|int
name|commonSuffixBytesPosEnd
init|=
name|compiledAutomaton
operator|.
name|commonSuffixRef
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|commonSuffixBytesPos
operator|<
name|commonSuffixBytesPosEnd
condition|)
block|{
if|if
condition|(
name|suffixBytes
index|[
name|suffixBytesPos
operator|++
index|]
operator|!=
name|commonSuffixBytes
index|[
name|commonSuffixBytesPos
operator|++
index|]
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("      skip: common suffix mismatch");
comment|// }
continue|continue
name|nextTerm
continue|;
block|}
block|}
block|}
comment|// TODO: maybe we should do the same linear test
comment|// that AutomatonTermsEnum does, so that if we
comment|// reach a part of the automaton where .* is
comment|// "temporarily" accepted, we just blindly .next()
comment|// until the limit
comment|// See if the term prefix matches the automaton:
name|int
name|state
init|=
name|currentFrame
operator|.
name|state
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|currentFrame
operator|.
name|suffix
condition|;
name|idx
operator|++
control|)
block|{
name|state
operator|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|state
argument_list|,
name|currentFrame
operator|.
name|suffixBytes
index|[
name|currentFrame
operator|.
name|startBytePos
operator|+
name|idx
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
operator|-
literal|1
condition|)
block|{
comment|// No match
comment|//System.out.println("    no s=" + state);
continue|continue
name|nextTerm
continue|;
block|}
else|else
block|{
comment|//System.out.println("    c s=" + state);
block|}
block|}
if|if
condition|(
name|isSubBlock
condition|)
block|{
comment|// Match!  Recurse:
comment|//if (DEBUG) System.out.println("      sub-block match to state=" + state + "; recurse fp=" + currentFrame.lastSubFP);
name|copyTerm
argument_list|()
expr_stmt|;
name|currentFrame
operator|=
name|pushFrame
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|//if (DEBUG) System.out.println("\n  frame ord=" + currentFrame.ord + " prefix=" + brToString(new BytesRef(term.bytes, term.offset, currentFrame.prefix)) + " state=" + currentFrame.state + " lastInFloor?=" + currentFrame.isLastInFloor + " fp=" + currentFrame.fp + " trans=" + (currentFrame.transitions.length == 0 ? "n/a" : currentFrame.transitions[currentFrame.transitionIndex]) + " outputPrefix=" + currentFrame.outputPrefix);
block|}
elseif|else
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|copyTerm
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("      term match to state=" + state + "; return term=" + brToString(term));
assert|assert
name|savedStartTerm
operator|==
literal|null
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|savedStartTerm
argument_list|)
operator|>
literal|0
operator|:
literal|"saveStartTerm="
operator|+
name|savedStartTerm
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" term="
operator|+
name|term
operator|.
name|utf8ToString
argument_list|()
assert|;
return|return
name|term
return|;
block|}
else|else
block|{
comment|//System.out.println("    no s=" + state);
block|}
block|}
block|}
DECL|method|copyTerm
specifier|private
name|void
name|copyTerm
parameter_list|()
block|{
comment|//System.out.println("      copyTerm cur.prefix=" + currentFrame.prefix + " cur.suffix=" + currentFrame.suffix + " first=" + (char) currentFrame.suffixBytes[currentFrame.startBytePos]);
specifier|final
name|int
name|len
init|=
name|currentFrame
operator|.
name|prefix
operator|+
name|currentFrame
operator|.
name|suffix
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|bytes
operator|.
name|length
operator|<
name|len
condition|)
block|{
name|term
operator|.
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|currentFrame
operator|.
name|suffixBytes
argument_list|,
name|currentFrame
operator|.
name|startBytePos
argument_list|,
name|term
operator|.
name|bytes
argument_list|,
name|currentFrame
operator|.
name|prefix
argument_list|,
name|currentFrame
operator|.
name|suffix
argument_list|)
expr_stmt|;
name|term
operator|.
name|length
operator|=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
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
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
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
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

