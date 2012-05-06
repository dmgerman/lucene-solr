begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.charfilter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
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
name|java
operator|.
name|io
operator|.
name|Reader
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|CharReader
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
name|CharStream
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
name|RollingCharBuffer
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
name|CharSequenceOutputs
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
comment|/**  * Simplistic {@link CharFilter} that applies the mappings  * contained in a {@link NormalizeCharMap} to the character  * stream, and correcting the resulting changes to the  * offsets.  Matching is greedy (longest pattern matching at  * a given point wins).  Replacement is allowed to be the  * empty string.  */
end_comment

begin_class
DECL|class|MappingCharFilter
specifier|public
class|class
name|MappingCharFilter
extends|extends
name|BaseCharFilter
block|{
DECL|field|outputs
specifier|private
specifier|final
name|Outputs
argument_list|<
name|CharsRef
argument_list|>
name|outputs
init|=
name|CharSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
DECL|field|map
specifier|private
specifier|final
name|FST
argument_list|<
name|CharsRef
argument_list|>
name|map
decl_stmt|;
DECL|field|fstReader
specifier|private
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|RollingCharBuffer
name|buffer
init|=
operator|new
name|RollingCharBuffer
argument_list|()
decl_stmt|;
DECL|field|scratchArc
specifier|private
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|CharsRef
argument_list|>
name|scratchArc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|CharsRef
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|cachedRootArcs
specifier|private
specifier|final
name|Map
argument_list|<
name|Character
argument_list|,
name|FST
operator|.
name|Arc
argument_list|<
name|CharsRef
argument_list|>
argument_list|>
name|cachedRootArcs
decl_stmt|;
DECL|field|replacement
specifier|private
name|CharsRef
name|replacement
decl_stmt|;
DECL|field|replacementPointer
specifier|private
name|int
name|replacementPointer
decl_stmt|;
DECL|field|inputOff
specifier|private
name|int
name|inputOff
decl_stmt|;
comment|/** Default constructor that takes a {@link CharStream}. */
DECL|method|MappingCharFilter
specifier|public
name|MappingCharFilter
parameter_list|(
name|NormalizeCharMap
name|normMap
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|map
operator|=
name|normMap
operator|.
name|map
expr_stmt|;
name|cachedRootArcs
operator|=
name|normMap
operator|.
name|cachedRootArcs
expr_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|fstReader
operator|=
name|map
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fstReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Easy-use constructor that takes a {@link Reader}. */
DECL|method|MappingCharFilter
specifier|public
name|MappingCharFilter
parameter_list|(
name|NormalizeCharMap
name|normMap
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|normMap
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|replacement
operator|=
literal|null
expr_stmt|;
name|inputOff
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("\nread");
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|replacement
operator|!=
literal|null
operator|&&
name|replacementPointer
operator|<
name|replacement
operator|.
name|length
condition|)
block|{
comment|//System.out.println("  return repl[" + replacementPointer + "]=" + replacement.chars[replacement.offset + replacementPointer]);
return|return
name|replacement
operator|.
name|chars
index|[
name|replacement
operator|.
name|offset
operator|+
name|replacementPointer
operator|++
index|]
return|;
block|}
comment|// TODO: a more efficient approach would be Aho/Corasick's
comment|// algorithm
comment|// (http://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_string_matching_algorithm)
comment|// or this generalizatio: www.cis.uni-muenchen.de/people/Schulz/Pub/dictle5.ps
comment|//
comment|// I think this would be (almost?) equivalent to 1) adding
comment|// epsilon arcs from all final nodes back to the init
comment|// node in the FST, 2) adding a .* (skip any char)
comment|// loop on the initial node, and 3) determinizing
comment|// that.  Then we would not have to restart matching
comment|// at each position.
name|int
name|lastMatchLen
init|=
operator|-
literal|1
decl_stmt|;
name|CharsRef
name|lastMatch
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|firstCH
init|=
name|buffer
operator|.
name|get
argument_list|(
name|inputOff
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstCH
operator|!=
operator|-
literal|1
condition|)
block|{
name|FST
operator|.
name|Arc
argument_list|<
name|CharsRef
argument_list|>
name|arc
init|=
name|cachedRootArcs
operator|.
name|get
argument_list|(
name|Character
operator|.
name|valueOf
argument_list|(
operator|(
name|char
operator|)
name|firstCH
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|arc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|FST
operator|.
name|targetHasArcs
argument_list|(
name|arc
argument_list|)
condition|)
block|{
comment|// Fast pass for single character match:
assert|assert
name|arc
operator|.
name|isFinal
argument_list|()
assert|;
name|lastMatchLen
operator|=
literal|1
expr_stmt|;
name|lastMatch
operator|=
name|arc
operator|.
name|output
expr_stmt|;
block|}
else|else
block|{
name|int
name|lookahead
init|=
literal|0
decl_stmt|;
name|CharsRef
name|output
init|=
name|arc
operator|.
name|output
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|lookahead
operator|++
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
comment|// Match! (to node is final)
name|lastMatchLen
operator|=
name|lookahead
expr_stmt|;
name|lastMatch
operator|=
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|nextFinalOutput
argument_list|)
expr_stmt|;
comment|// Greedy: keep searching to see if there's a
comment|// longer match...
block|}
if|if
condition|(
operator|!
name|FST
operator|.
name|targetHasArcs
argument_list|(
name|arc
argument_list|)
condition|)
block|{
break|break;
block|}
name|int
name|ch
init|=
name|buffer
operator|.
name|get
argument_list|(
name|inputOff
operator|+
name|lookahead
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
if|if
condition|(
operator|(
name|arc
operator|=
name|map
operator|.
name|findTargetArc
argument_list|(
name|ch
argument_list|,
name|arc
argument_list|,
name|scratchArc
argument_list|,
name|fstReader
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
comment|// Dead end
break|break;
block|}
name|output
operator|=
name|outputs
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
block|}
block|}
block|}
block|}
if|if
condition|(
name|lastMatch
operator|!=
literal|null
condition|)
block|{
name|inputOff
operator|+=
name|lastMatchLen
expr_stmt|;
comment|//System.out.println("  match!  len=" + lastMatchLen + " repl=" + lastMatch);
specifier|final
name|int
name|diff
init|=
name|lastMatchLen
operator|-
name|lastMatch
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|prevCumulativeDiff
init|=
name|getLastCumulativeDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|>
literal|0
condition|)
block|{
comment|// Replacement is shorter than matched input:
name|addOffCorrectMap
argument_list|(
name|inputOff
operator|-
name|diff
operator|-
name|prevCumulativeDiff
argument_list|,
name|prevCumulativeDiff
operator|+
name|diff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Replacement is longer than matched input: remap
comment|// the "extra" chars all back to the same input
comment|// offset:
specifier|final
name|int
name|outputStart
init|=
name|inputOff
operator|-
name|prevCumulativeDiff
decl_stmt|;
for|for
control|(
name|int
name|extraIDX
init|=
literal|0
init|;
name|extraIDX
operator|<
operator|-
name|diff
condition|;
name|extraIDX
operator|++
control|)
block|{
name|addOffCorrectMap
argument_list|(
name|outputStart
operator|+
name|extraIDX
argument_list|,
name|prevCumulativeDiff
operator|-
name|extraIDX
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|replacement
operator|=
name|lastMatch
expr_stmt|;
name|replacementPointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|ret
init|=
name|buffer
operator|.
name|get
argument_list|(
name|inputOff
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
operator|-
literal|1
condition|)
block|{
name|inputOff
operator|++
expr_stmt|;
name|buffer
operator|.
name|freeBefore
argument_list|(
name|inputOff
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numRead
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off
operator|+
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
break|break;
name|cbuf
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
name|numRead
operator|++
expr_stmt|;
block|}
return|return
name|numRead
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|numRead
return|;
block|}
block|}
end_class

end_unit

