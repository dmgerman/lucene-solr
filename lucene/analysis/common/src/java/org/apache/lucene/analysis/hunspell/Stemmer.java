begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|util
operator|.
name|CharArraySet
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
name|IntsRef
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Stemmer uses the affix rules declared in the Dictionary to generate one or more stems for a word.  It  * conforms to the algorithm in the original hunspell algorithm, including recursive suffix stripping.  */
end_comment

begin_class
DECL|class|Stemmer
specifier|final
class|class
name|Stemmer
block|{
DECL|field|dictionary
specifier|private
specifier|final
name|Dictionary
name|dictionary
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|StringBuilder
name|segment
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|affixReader
specifier|private
specifier|final
name|ByteArrayDataInput
name|affixReader
decl_stmt|;
comment|// used for normalization
DECL|field|scratchSegment
specifier|private
specifier|final
name|StringBuilder
name|scratchSegment
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|scratchBuffer
specifier|private
name|char
name|scratchBuffer
index|[]
init|=
operator|new
name|char
index|[
literal|32
index|]
decl_stmt|;
comment|/**    * Constructs a new Stemmer which will use the provided Dictionary to create its stems.    *    * @param dictionary Dictionary that will be used to create the stems    */
DECL|method|Stemmer
specifier|public
name|Stemmer
parameter_list|(
name|Dictionary
name|dictionary
parameter_list|)
block|{
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
name|this
operator|.
name|affixReader
operator|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|dictionary
operator|.
name|affixData
argument_list|)
expr_stmt|;
block|}
comment|/**    * Find the stem(s) of the provided word.    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|stem
specifier|public
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stem
parameter_list|(
name|String
name|word
parameter_list|)
block|{
return|return
name|stem
argument_list|(
name|word
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|word
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Find the stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|stem
specifier|public
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stem
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|dictionary
operator|.
name|needsInputCleaning
condition|)
block|{
name|scratchSegment
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|scratchSegment
operator|.
name|append
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|CharSequence
name|cleaned
init|=
name|dictionary
operator|.
name|cleanInput
argument_list|(
name|scratchSegment
argument_list|,
name|segment
argument_list|)
decl_stmt|;
name|scratchBuffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scratchBuffer
argument_list|,
name|cleaned
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|length
operator|=
name|segment
operator|.
name|length
argument_list|()
expr_stmt|;
name|segment
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|length
argument_list|,
name|scratchBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|word
operator|=
name|scratchBuffer
expr_stmt|;
block|}
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|CharsRef
argument_list|>
argument_list|()
decl_stmt|;
name|IntsRef
name|forms
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|forms
operator|!=
literal|null
condition|)
block|{
comment|// TODO: some forms should not be added, e.g. ONLYINCOMPOUND
comment|// just because it exists, does not make it valid...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|forms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stems
operator|.
name|add
argument_list|(
name|newStem
argument_list|(
name|word
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stems
return|;
block|}
comment|/**    * Find the unique stem(s) of the provided word    *     * @param word Word to find the stems for    * @return List of stems for the word    */
DECL|method|uniqueStems
specifier|public
name|List
argument_list|<
name|CharsRef
argument_list|>
name|uniqueStems
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stems
init|=
name|stem
argument_list|(
name|word
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|stems
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
return|return
name|stems
return|;
block|}
name|CharArraySet
name|terms
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|8
argument_list|,
name|dictionary
operator|.
name|ignoreCase
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CharsRef
argument_list|>
name|deduped
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CharsRef
name|s
range|:
name|stems
control|)
block|{
if|if
condition|(
operator|!
name|terms
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|deduped
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|deduped
return|;
block|}
DECL|method|newStem
specifier|private
name|CharsRef
name|newStem
parameter_list|(
name|char
name|buffer
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|dictionary
operator|.
name|needsOutputCleaning
condition|)
block|{
name|scratchSegment
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|scratchSegment
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|dictionary
operator|.
name|oconv
argument_list|,
name|scratchSegment
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
name|char
name|cleaned
index|[]
init|=
operator|new
name|char
index|[
name|scratchSegment
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|scratchSegment
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|cleaned
operator|.
name|length
argument_list|,
name|cleaned
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|CharsRef
argument_list|(
name|cleaned
argument_list|,
literal|0
argument_list|,
name|cleaned
operator|.
name|length
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|CharsRef
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
comment|// ================================================= Helper Methods ================================================
comment|/**    * Generates a list of stems for the provided word    *    * @param word Word to generate the stems for    * @param previous previous affix that was removed (so we dont remove same one twice)    * @param prevFlag Flag from a previous stemming step that need to be cross-checked with any affixes in this recursive step    * @param prefixFlag flag of the most inner removed prefix, so that when removing a suffix, its also checked against the word    * @param recursionDepth current recursiondepth    * @param doPrefix true if we should remove prefixes    * @param doSuffix true if we should remove suffixes    * @param previousWasPrefix true if the previous removal was a prefix:    *        if we are removing a suffix, and it has no continuation requirements, its ok.    *        but two prefixes (COMPLEXPREFIXES) or two suffixes must have continuation requirements to recurse.     * @param circumfix true if the previous prefix removal was signed as a circumfix    *        this means inner most suffix must also contain circumfix flag.    * @return List of stems, or empty list if no stems are found    */
DECL|method|stem
specifier|private
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stem
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|previous
parameter_list|,
name|int
name|prevFlag
parameter_list|,
name|int
name|prefixFlag
parameter_list|,
name|int
name|recursionDepth
parameter_list|,
name|boolean
name|doPrefix
parameter_list|,
name|boolean
name|doSuffix
parameter_list|,
name|boolean
name|previousWasPrefix
parameter_list|,
name|boolean
name|circumfix
parameter_list|)
block|{
comment|// TODO: allow this stuff to be reused by tokenfilter
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|CharsRef
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|doPrefix
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|IntsRef
name|prefixes
init|=
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|prefixes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|prefix
init|=
name|prefixes
operator|.
name|ints
index|[
name|prefixes
operator|.
name|offset
operator|+
name|j
index|]
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
name|previous
condition|)
block|{
continue|continue;
block|}
name|affixReader
operator|.
name|setPosition
argument_list|(
literal|8
operator|*
name|prefix
argument_list|)
expr_stmt|;
name|char
name|flag
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|char
name|stripOrd
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|int
name|condition
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|boolean
name|crossProduct
init|=
operator|(
name|condition
operator|&
literal|1
operator|)
operator|==
literal|1
decl_stmt|;
name|condition
operator|>>>=
literal|1
expr_stmt|;
name|char
name|append
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|compatible
decl_stmt|;
if|if
condition|(
name|recursionDepth
operator|==
literal|0
condition|)
block|{
name|compatible
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|crossProduct
condition|)
block|{
comment|// cross check incoming continuation class (flag of previous affix) against list.
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|append
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|char
name|appendFlags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
assert|assert
name|prevFlag
operator|>=
literal|0
assert|;
name|compatible
operator|=
name|hasCrossCheckedFlag
argument_list|(
operator|(
name|char
operator|)
name|prevFlag
argument_list|,
name|appendFlags
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compatible
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|compatible
condition|)
block|{
name|int
name|deAffixedStart
init|=
name|i
decl_stmt|;
name|int
name|deAffixedLength
init|=
name|length
operator|-
name|deAffixedStart
decl_stmt|;
name|dictionary
operator|.
name|stripLookup
operator|.
name|get
argument_list|(
name|stripOrd
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|String
name|strippedWord
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|scratch
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|word
argument_list|,
name|deAffixedStart
argument_list|,
name|deAffixedLength
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stemList
init|=
name|applyAffix
argument_list|(
name|strippedWord
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|()
argument_list|,
name|prefix
argument_list|,
operator|-
literal|1
argument_list|,
name|recursionDepth
argument_list|,
literal|true
argument_list|,
name|circumfix
argument_list|)
decl_stmt|;
name|stems
operator|.
name|addAll
argument_list|(
name|stemList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|doSuffix
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IntsRef
name|suffixes
init|=
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
name|word
argument_list|,
name|i
argument_list|,
name|length
operator|-
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffixes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|suffixes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|suffix
init|=
name|suffixes
operator|.
name|ints
index|[
name|suffixes
operator|.
name|offset
operator|+
name|j
index|]
decl_stmt|;
if|if
condition|(
name|suffix
operator|==
name|previous
condition|)
block|{
continue|continue;
block|}
name|affixReader
operator|.
name|setPosition
argument_list|(
literal|8
operator|*
name|suffix
argument_list|)
expr_stmt|;
name|char
name|flag
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|char
name|stripOrd
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|int
name|condition
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|boolean
name|crossProduct
init|=
operator|(
name|condition
operator|&
literal|1
operator|)
operator|==
literal|1
decl_stmt|;
name|condition
operator|>>>=
literal|1
expr_stmt|;
name|char
name|append
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|compatible
decl_stmt|;
if|if
condition|(
name|recursionDepth
operator|==
literal|0
condition|)
block|{
name|compatible
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|crossProduct
condition|)
block|{
comment|// cross check incoming continuation class (flag of previous affix) against list.
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|append
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|char
name|appendFlags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
assert|assert
name|prevFlag
operator|>=
literal|0
assert|;
name|compatible
operator|=
name|hasCrossCheckedFlag
argument_list|(
operator|(
name|char
operator|)
name|prevFlag
argument_list|,
name|appendFlags
argument_list|,
name|previousWasPrefix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compatible
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|compatible
condition|)
block|{
name|int
name|appendLength
init|=
name|length
operator|-
name|i
decl_stmt|;
name|int
name|deAffixedLength
init|=
name|length
operator|-
name|appendLength
decl_stmt|;
comment|// TODO: can we do this in-place?
name|dictionary
operator|.
name|stripLookup
operator|.
name|get
argument_list|(
name|stripOrd
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|String
name|strippedWord
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|deAffixedLength
argument_list|)
operator|.
name|append
argument_list|(
name|scratch
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stemList
init|=
name|applyAffix
argument_list|(
name|strippedWord
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|strippedWord
operator|.
name|length
argument_list|()
argument_list|,
name|suffix
argument_list|,
name|prefixFlag
argument_list|,
name|recursionDepth
argument_list|,
literal|false
argument_list|,
name|circumfix
argument_list|)
decl_stmt|;
name|stems
operator|.
name|addAll
argument_list|(
name|stemList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|stems
return|;
block|}
comment|/**    * Applies the affix rule to the given word, producing a list of stems if any are found    *    * @param strippedWord Word the affix has been removed and the strip added    * @param length valid length of stripped word    * @param affix HunspellAffix representing the affix rule itself    * @param prefixFlag when we already stripped a prefix, we cant simply recurse and check the suffix, unless both are compatible    *                   so we must check dictionary form against both to add it as a stem!    * @param recursionDepth current recursion depth    * @param prefix true if we are removing a prefix (false if its a suffix)    * @return List of stems for the word, or an empty list if none are found    */
DECL|method|applyAffix
name|List
argument_list|<
name|CharsRef
argument_list|>
name|applyAffix
parameter_list|(
name|char
name|strippedWord
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|affix
parameter_list|,
name|int
name|prefixFlag
parameter_list|,
name|int
name|recursionDepth
parameter_list|,
name|boolean
name|prefix
parameter_list|,
name|boolean
name|circumfix
parameter_list|)
block|{
name|segment
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|segment
operator|.
name|append
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// TODO: just pass this in from before, no need to decode it twice
name|affixReader
operator|.
name|setPosition
argument_list|(
literal|8
operator|*
name|affix
argument_list|)
expr_stmt|;
name|char
name|flag
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|affixReader
operator|.
name|skipBytes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// strip
name|int
name|condition
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|boolean
name|crossProduct
init|=
operator|(
name|condition
operator|&
literal|1
operator|)
operator|==
literal|1
decl_stmt|;
name|condition
operator|>>>=
literal|1
expr_stmt|;
name|char
name|append
init|=
call|(
name|char
call|)
argument_list|(
name|affixReader
operator|.
name|readShort
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
name|Pattern
name|pattern
init|=
name|dictionary
operator|.
name|patterns
operator|.
name|get
argument_list|(
name|condition
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|pattern
operator|.
name|matcher
argument_list|(
name|segment
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stems
init|=
operator|new
name|ArrayList
argument_list|<
name|CharsRef
argument_list|>
argument_list|()
decl_stmt|;
name|IntsRef
name|forms
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
name|strippedWord
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|forms
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|forms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|forms
operator|.
name|ints
index|[
name|forms
operator|.
name|offset
operator|+
name|i
index|]
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|char
name|wordFlags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
if|if
condition|(
name|Dictionary
operator|.
name|hasFlag
argument_list|(
name|wordFlags
argument_list|,
name|flag
argument_list|)
condition|)
block|{
comment|// confusing: in this one exception, we already chained the first prefix against the second,
comment|// so it doesnt need to be checked against the word
name|boolean
name|chainedPrefix
init|=
name|dictionary
operator|.
name|complexPrefixes
operator|&&
name|recursionDepth
operator|==
literal|1
operator|&&
name|prefix
decl_stmt|;
if|if
condition|(
name|chainedPrefix
operator|==
literal|false
operator|&&
name|prefixFlag
operator|>=
literal|0
operator|&&
operator|!
name|Dictionary
operator|.
name|hasFlag
argument_list|(
name|wordFlags
argument_list|,
operator|(
name|char
operator|)
name|prefixFlag
argument_list|)
condition|)
block|{
comment|// see if we can chain prefix thru the suffix continuation class (only if it has any!)
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|append
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|char
name|appendFlags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasCrossCheckedFlag
argument_list|(
operator|(
name|char
operator|)
name|prefixFlag
argument_list|,
name|appendFlags
argument_list|,
literal|false
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
comment|// if circumfix was previously set by a prefix, we must check this suffix,
comment|// to ensure it has it, and vice versa
if|if
condition|(
name|dictionary
operator|.
name|circumfix
operator|!=
operator|-
literal|1
condition|)
block|{
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|append
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|char
name|appendFlags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
name|boolean
name|suffixCircumfix
init|=
name|Dictionary
operator|.
name|hasFlag
argument_list|(
name|appendFlags
argument_list|,
operator|(
name|char
operator|)
name|dictionary
operator|.
name|circumfix
argument_list|)
decl_stmt|;
if|if
condition|(
name|circumfix
operator|!=
name|suffixCircumfix
condition|)
block|{
continue|continue;
block|}
block|}
name|stems
operator|.
name|add
argument_list|(
name|newStem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// if a circumfix flag is defined in the dictionary, and we are a prefix, we need to check if we have that flag
if|if
condition|(
name|dictionary
operator|.
name|circumfix
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|circumfix
operator|&&
name|prefix
condition|)
block|{
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|append
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|char
name|appendFlags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
name|circumfix
operator|=
name|Dictionary
operator|.
name|hasFlag
argument_list|(
name|appendFlags
argument_list|,
operator|(
name|char
operator|)
name|dictionary
operator|.
name|circumfix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|crossProduct
condition|)
block|{
if|if
condition|(
name|recursionDepth
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|prefix
condition|)
block|{
comment|// we took away the first prefix.
comment|// COMPLEXPREFIXES = true:  combine with a second prefix and another suffix
comment|// COMPLEXPREFIXES = false: combine with another suffix
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|,
name|affix
argument_list|,
name|flag
argument_list|,
name|flag
argument_list|,
operator|++
name|recursionDepth
argument_list|,
name|dictionary
operator|.
name|complexPrefixes
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|circumfix
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|dictionary
operator|.
name|complexPrefixes
condition|)
block|{
comment|// we took away a suffix.
comment|// COMPLEXPREFIXES = true: we don't recurse! only one suffix allowed
comment|// COMPLEXPREFIXES = false: combine with another suffix
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|,
name|affix
argument_list|,
name|flag
argument_list|,
name|prefixFlag
argument_list|,
operator|++
name|recursionDepth
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|circumfix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|recursionDepth
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|prefix
operator|&&
name|dictionary
operator|.
name|complexPrefixes
condition|)
block|{
comment|// we took away the second prefix: go look for another suffix
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|,
name|affix
argument_list|,
name|flag
argument_list|,
name|flag
argument_list|,
operator|++
name|recursionDepth
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|circumfix
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|==
literal|false
operator|&&
name|dictionary
operator|.
name|complexPrefixes
operator|==
literal|false
condition|)
block|{
comment|// we took away a prefix, then a suffix: go look for another suffix
name|stems
operator|.
name|addAll
argument_list|(
name|stem
argument_list|(
name|strippedWord
argument_list|,
name|length
argument_list|,
name|affix
argument_list|,
name|flag
argument_list|,
name|prefixFlag
argument_list|,
operator|++
name|recursionDepth
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|circumfix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|stems
return|;
block|}
comment|/**    * Checks if the given flag cross checks with the given array of flags    *    * @param flag Flag to cross check with the array of flags    * @param flags Array of flags to cross check against.  Can be {@code null}    * @return {@code true} if the flag is found in the array or the array is {@code null}, {@code false} otherwise    */
DECL|method|hasCrossCheckedFlag
specifier|private
name|boolean
name|hasCrossCheckedFlag
parameter_list|(
name|char
name|flag
parameter_list|,
name|char
index|[]
name|flags
parameter_list|,
name|boolean
name|matchEmpty
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|.
name|length
operator|==
literal|0
operator|&&
name|matchEmpty
operator|)
operator|||
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|flags
argument_list|,
name|flag
argument_list|)
operator|>=
literal|0
return|;
block|}
block|}
end_class

end_unit

