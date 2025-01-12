begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
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
name|text
operator|.
name|ParseException
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
name|Arrays
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
name|HashSet
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
name|analysis
operator|.
name|Analyzer
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
name|TokenStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|BytesRefHash
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
name|IntsRefBuilder
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
name|Util
import|;
end_import

begin_comment
comment|/**  * A map of synonyms, keys and values are phrases.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SynonymMap
specifier|public
class|class
name|SynonymMap
block|{
comment|/** for multiword support, you must separate words with this separator */
DECL|field|WORD_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|WORD_SEPARATOR
init|=
literal|0
decl_stmt|;
comment|/** map&lt;input word, list&lt;ord&gt;&gt; */
DECL|field|fst
specifier|public
specifier|final
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
decl_stmt|;
comment|/** map&lt;ord, outputword&gt; */
DECL|field|words
specifier|public
specifier|final
name|BytesRefHash
name|words
decl_stmt|;
comment|/** maxHorizontalContext: maximum context we need on the tokenstream */
DECL|field|maxHorizontalContext
specifier|public
specifier|final
name|int
name|maxHorizontalContext
decl_stmt|;
DECL|method|SynonymMap
specifier|public
name|SynonymMap
parameter_list|(
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
parameter_list|,
name|BytesRefHash
name|words
parameter_list|,
name|int
name|maxHorizontalContext
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
name|words
operator|=
name|words
expr_stmt|;
name|this
operator|.
name|maxHorizontalContext
operator|=
name|maxHorizontalContext
expr_stmt|;
block|}
comment|/**    * Builds an FSTSynonymMap.    *<p>    * Call add() until you have added all the mappings, then call build() to get an FSTSynonymMap    * @lucene.experimental    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|workingSet
specifier|private
specifier|final
name|HashMap
argument_list|<
name|CharsRef
argument_list|,
name|MapEntry
argument_list|>
name|workingSet
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|words
specifier|private
specifier|final
name|BytesRefHash
name|words
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
DECL|field|utf8Scratch
specifier|private
specifier|final
name|BytesRefBuilder
name|utf8Scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|maxHorizontalContext
specifier|private
name|int
name|maxHorizontalContext
decl_stmt|;
DECL|field|dedup
specifier|private
specifier|final
name|boolean
name|dedup
decl_stmt|;
comment|/** Default constructor, passes {@code dedup=true}. */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** If dedup is true then identical rules (same input,      *  same output) will be added only once. */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|boolean
name|dedup
parameter_list|)
block|{
name|this
operator|.
name|dedup
operator|=
name|dedup
expr_stmt|;
block|}
DECL|class|MapEntry
specifier|private
specifier|static
class|class
name|MapEntry
block|{
DECL|field|includeOrig
name|boolean
name|includeOrig
decl_stmt|;
comment|// we could sort for better sharing ultimately, but it could confuse people
DECL|field|ords
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|ords
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
block|}
comment|/** Sugar: just joins the provided terms with {@link      *  SynonymMap#WORD_SEPARATOR}.  reuse and its chars      *  must not be null. */
DECL|method|join
specifier|public
specifier|static
name|CharsRef
name|join
parameter_list|(
name|String
index|[]
name|words
parameter_list|,
name|CharsRefBuilder
name|reuse
parameter_list|)
block|{
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|buffer
init|=
name|reuse
operator|.
name|chars
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|words
control|)
block|{
specifier|final
name|int
name|wordLen
init|=
name|word
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|needed
init|=
operator|(
literal|0
operator|==
name|upto
condition|?
name|wordLen
else|:
literal|1
operator|+
name|upto
operator|+
name|wordLen
operator|)
decl_stmt|;
comment|// Add 1 for WORD_SEPARATOR
if|if
condition|(
name|needed
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
name|reuse
operator|.
name|grow
argument_list|(
name|needed
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|reuse
operator|.
name|chars
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|upto
operator|>
literal|0
condition|)
block|{
name|buffer
index|[
name|upto
operator|++
index|]
operator|=
name|SynonymMap
operator|.
name|WORD_SEPARATOR
expr_stmt|;
block|}
name|word
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|wordLen
argument_list|,
name|buffer
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|wordLen
expr_stmt|;
block|}
name|reuse
operator|.
name|setLength
argument_list|(
name|upto
argument_list|)
expr_stmt|;
return|return
name|reuse
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** only used for asserting! */
DECL|method|hasHoles
specifier|private
name|boolean
name|hasHoles
parameter_list|(
name|CharsRef
name|chars
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|chars
operator|.
name|offset
operator|+
name|chars
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|chars
operator|.
name|offset
operator|+
literal|1
init|;
name|idx
operator|<
name|end
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|chars
operator|.
name|chars
index|[
name|idx
index|]
operator|==
name|SynonymMap
operator|.
name|WORD_SEPARATOR
operator|&&
name|chars
operator|.
name|chars
index|[
name|idx
operator|-
literal|1
index|]
operator|==
name|SynonymMap
operator|.
name|WORD_SEPARATOR
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|chars
operator|.
name|chars
index|[
name|chars
operator|.
name|offset
index|]
operator|==
literal|'\u0000'
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|chars
operator|.
name|chars
index|[
name|chars
operator|.
name|offset
operator|+
name|chars
operator|.
name|length
operator|-
literal|1
index|]
operator|==
literal|'\u0000'
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|// NOTE: while it's tempting to make this public, since
comment|// caller's parser likely knows the
comment|// numInput/numOutputWords, sneaky exceptions, much later
comment|// on, will result if these values are wrong; so we always
comment|// recompute ourselves to be safe:
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|CharsRef
name|input
parameter_list|,
name|int
name|numInputWords
parameter_list|,
name|CharsRef
name|output
parameter_list|,
name|int
name|numOutputWords
parameter_list|,
name|boolean
name|includeOrig
parameter_list|)
block|{
comment|// first convert to UTF-8
if|if
condition|(
name|numInputWords
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numInputWords must be> 0 (got "
operator|+
name|numInputWords
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|length
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"input.length must be> 0 (got "
operator|+
name|input
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|numOutputWords
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numOutputWords must be> 0 (got "
operator|+
name|numOutputWords
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|output
operator|.
name|length
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"output.length must be> 0 (got "
operator|+
name|output
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
assert|assert
operator|!
name|hasHoles
argument_list|(
name|input
argument_list|)
operator|:
literal|"input has holes: "
operator|+
name|input
assert|;
assert|assert
operator|!
name|hasHoles
argument_list|(
name|output
argument_list|)
operator|:
literal|"output has holes: "
operator|+
name|output
assert|;
comment|//System.out.println("fmap.add input=" + input + " numInputWords=" + numInputWords + " output=" + output + " numOutputWords=" + numOutputWords);
name|utf8Scratch
operator|.
name|copyChars
argument_list|(
name|output
operator|.
name|chars
argument_list|,
name|output
operator|.
name|offset
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// lookup in hash
name|int
name|ord
init|=
name|words
operator|.
name|add
argument_list|(
name|utf8Scratch
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
comment|// already exists in our hash
name|ord
operator|=
operator|(
operator|-
name|ord
operator|)
operator|-
literal|1
expr_stmt|;
comment|//System.out.println("  output=" + output + " old ord=" + ord);
block|}
else|else
block|{
comment|//System.out.println("  output=" + output + " new ord=" + ord);
block|}
name|MapEntry
name|e
init|=
name|workingSet
operator|.
name|get
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
operator|new
name|MapEntry
argument_list|()
expr_stmt|;
name|workingSet
operator|.
name|put
argument_list|(
name|CharsRef
operator|.
name|deepCopyOf
argument_list|(
name|input
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// make a copy, since we will keep around in our map
block|}
name|e
operator|.
name|ords
operator|.
name|add
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|e
operator|.
name|includeOrig
operator||=
name|includeOrig
expr_stmt|;
name|maxHorizontalContext
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxHorizontalContext
argument_list|,
name|numInputWords
argument_list|)
expr_stmt|;
name|maxHorizontalContext
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxHorizontalContext
argument_list|,
name|numOutputWords
argument_list|)
expr_stmt|;
block|}
DECL|method|countWords
specifier|private
name|int
name|countWords
parameter_list|(
name|CharsRef
name|chars
parameter_list|)
block|{
name|int
name|wordCount
init|=
literal|1
decl_stmt|;
name|int
name|upto
init|=
name|chars
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|chars
operator|.
name|offset
operator|+
name|chars
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|limit
condition|)
block|{
if|if
condition|(
name|chars
operator|.
name|chars
index|[
name|upto
operator|++
index|]
operator|==
name|SynonymMap
operator|.
name|WORD_SEPARATOR
condition|)
block|{
name|wordCount
operator|++
expr_stmt|;
block|}
block|}
return|return
name|wordCount
return|;
block|}
comment|/**      * Add a phrase-&gt;phrase synonym mapping.      * Phrases are character sequences where words are      * separated with character zero (U+0000).  Empty words      * (two U+0000s in a row) are not allowed in the input nor      * the output!      *       * @param input input phrase      * @param output output phrase      * @param includeOrig true if the original should be included      */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|CharsRef
name|input
parameter_list|,
name|CharsRef
name|output
parameter_list|,
name|boolean
name|includeOrig
parameter_list|)
block|{
name|add
argument_list|(
name|input
argument_list|,
name|countWords
argument_list|(
name|input
argument_list|)
argument_list|,
name|output
argument_list|,
name|countWords
argument_list|(
name|output
argument_list|)
argument_list|,
name|includeOrig
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an {@link SynonymMap} and returns it.      */
DECL|method|build
specifier|public
name|SynonymMap
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteSequenceOutputs
name|outputs
init|=
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
comment|// TODO: are we using the best sharing options?
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
name|Builder
argument_list|<
name|BytesRef
argument_list|>
name|builder
init|=
operator|new
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
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|ByteArrayDataOutput
name|scratchOutput
init|=
operator|new
name|ByteArrayDataOutput
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|dedupSet
decl_stmt|;
if|if
condition|(
name|dedup
condition|)
block|{
name|dedupSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dedupSet
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|spare
init|=
operator|new
name|byte
index|[
literal|5
index|]
decl_stmt|;
name|Set
argument_list|<
name|CharsRef
argument_list|>
name|keys
init|=
name|workingSet
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|CharsRef
name|sortedKeys
index|[]
init|=
name|keys
operator|.
name|toArray
argument_list|(
operator|new
name|CharsRef
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sortedKeys
argument_list|,
name|CharsRef
operator|.
name|getUTF16SortedAsUTF8Comparator
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IntsRefBuilder
name|scratchIntsRef
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
comment|//System.out.println("fmap.build");
for|for
control|(
name|int
name|keyIdx
init|=
literal|0
init|;
name|keyIdx
operator|<
name|sortedKeys
operator|.
name|length
condition|;
name|keyIdx
operator|++
control|)
block|{
name|CharsRef
name|input
init|=
name|sortedKeys
index|[
name|keyIdx
index|]
decl_stmt|;
name|MapEntry
name|output
init|=
name|workingSet
operator|.
name|get
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|int
name|numEntries
init|=
name|output
operator|.
name|ords
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// output size, assume the worst case
name|int
name|estimatedSize
init|=
literal|5
operator|+
name|numEntries
operator|*
literal|5
decl_stmt|;
comment|// numEntries + one ord for each entry
name|scratch
operator|.
name|grow
argument_list|(
name|estimatedSize
argument_list|)
expr_stmt|;
name|scratchOutput
operator|.
name|reset
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// now write our output data:
name|int
name|count
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
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dedupSet
operator|!=
literal|null
condition|)
block|{
comment|// box once
specifier|final
name|Integer
name|ent
init|=
name|output
operator|.
name|ords
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|dedupSet
operator|.
name|contains
argument_list|(
name|ent
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|dedupSet
operator|.
name|add
argument_list|(
name|ent
argument_list|)
expr_stmt|;
block|}
name|scratchOutput
operator|.
name|writeVInt
argument_list|(
name|output
operator|.
name|ords
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
specifier|final
name|int
name|pos
init|=
name|scratchOutput
operator|.
name|getPosition
argument_list|()
decl_stmt|;
name|scratchOutput
operator|.
name|writeVInt
argument_list|(
name|count
operator|<<
literal|1
operator||
operator|(
name|output
operator|.
name|includeOrig
condition|?
literal|0
else|:
literal|1
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|pos2
init|=
name|scratchOutput
operator|.
name|getPosition
argument_list|()
decl_stmt|;
specifier|final
name|int
name|vIntLen
init|=
name|pos2
operator|-
name|pos
decl_stmt|;
comment|// Move the count + includeOrig to the front of the byte[]:
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|pos
argument_list|,
name|spare
argument_list|,
literal|0
argument_list|,
name|vIntLen
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|vIntLen
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|spare
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|vIntLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|dedupSet
operator|!=
literal|null
condition|)
block|{
name|dedupSet
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|scratch
operator|.
name|setLength
argument_list|(
name|scratchOutput
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("  add input=" + input + " output=" + scratch + " offset=" + scratch.offset + " length=" + scratch.length + " count=" + count);
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toUTF32
argument_list|(
name|input
argument_list|,
name|scratchIntsRef
argument_list|)
argument_list|,
name|scratch
operator|.
name|toBytesRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
return|return
operator|new
name|SynonymMap
argument_list|(
name|fst
argument_list|,
name|words
argument_list|,
name|maxHorizontalContext
argument_list|)
return|;
block|}
block|}
comment|/**    * Abstraction for parsing synonym files.    *    * @lucene.experimental    */
DECL|class|Parser
specifier|public
specifier|static
specifier|abstract
class|class
name|Parser
extends|extends
name|Builder
block|{
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|method|Parser
specifier|public
name|Parser
parameter_list|(
name|boolean
name|dedup
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
name|dedup
argument_list|)
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/**      * Parse the given input, adding synonyms to the inherited {@link Builder}.      * @param in The input to parse      */
DECL|method|parse
specifier|public
specifier|abstract
name|void
name|parse
parameter_list|(
name|Reader
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
function_decl|;
comment|/** Sugar: analyzes the text with the analyzer and      *  separates by {@link SynonymMap#WORD_SEPARATOR}.      *  reuse and its chars must not be null. */
DECL|method|analyze
specifier|public
name|CharsRef
name|analyze
parameter_list|(
name|String
name|text
parameter_list|,
name|CharsRefBuilder
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|text
argument_list|)
init|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|reuse
operator|.
name|clear
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|length
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"term: "
operator|+
name|text
operator|+
literal|" analyzed to a zero-length token"
argument_list|)
throw|;
block|}
if|if
condition|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"term: "
operator|+
name|text
operator|+
literal|" analyzed to a token with posinc != 1"
argument_list|)
throw|;
block|}
name|reuse
operator|.
name|grow
argument_list|(
name|reuse
operator|.
name|length
argument_list|()
operator|+
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|/* current + word + separator */
name|int
name|end
init|=
name|reuse
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|reuse
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|reuse
operator|.
name|setCharAt
argument_list|(
name|end
operator|++
argument_list|,
name|SynonymMap
operator|.
name|WORD_SEPARATOR
argument_list|)
expr_stmt|;
name|reuse
operator|.
name|setLength
argument_list|(
name|reuse
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|reuse
operator|.
name|chars
argument_list|()
argument_list|,
name|end
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|reuse
operator|.
name|setLength
argument_list|(
name|reuse
operator|.
name|length
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reuse
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"term: "
operator|+
name|text
operator|+
literal|" was completely eliminated by analyzer"
argument_list|)
throw|;
block|}
return|return
name|reuse
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

