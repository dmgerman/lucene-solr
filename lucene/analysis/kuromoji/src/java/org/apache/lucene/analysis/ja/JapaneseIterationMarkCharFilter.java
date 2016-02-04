begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
package|;
end_package

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
name|CharFilter
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
name|RollingCharBuffer
import|;
end_import

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

begin_comment
comment|/**  * Normalizes Japanese horizontal iteration marks (odoriji) to their expanded form.  *<p>  * Sequences of iteration marks are supported.  In case an illegal sequence of iteration  * marks is encountered, the implementation emits the illegal source character as-is  * without considering its script.  For example, with input "?ã", we get  * "??" even though the question mark isn't hiragana.  *</p>  *<p>  * Note that a full stop punctuation character "ã" (U+3002) can not be iterated  * (see below). Iteration marks themselves can be emitted in case they are illegal,  * i.e. if they go back past the beginning of the character stream.  *</p>  *<p>  * The implementation buffers input until a full stop punctuation character (U+3002)  * or EOF is reached in order to not keep a copy of the character stream in memory.  * Vertical iteration marks, which are even rarer than horizontal iteration marks in  * contemporary Japanese, are unsupported.  *</p>  */
end_comment

begin_class
DECL|class|JapaneseIterationMarkCharFilter
specifier|public
class|class
name|JapaneseIterationMarkCharFilter
extends|extends
name|CharFilter
block|{
comment|/** Normalize kanji iteration marks by default */
DECL|field|NORMALIZE_KANJI_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|NORMALIZE_KANJI_DEFAULT
init|=
literal|true
decl_stmt|;
comment|/** Normalize kana iteration marks by default */
DECL|field|NORMALIZE_KANA_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|NORMALIZE_KANA_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|KANJI_ITERATION_MARK
specifier|private
specifier|static
specifier|final
name|char
name|KANJI_ITERATION_MARK
init|=
literal|'\u3005'
decl_stmt|;
comment|// ã
DECL|field|HIRAGANA_ITERATION_MARK
specifier|private
specifier|static
specifier|final
name|char
name|HIRAGANA_ITERATION_MARK
init|=
literal|'\u309d'
decl_stmt|;
comment|// ã
DECL|field|HIRAGANA_VOICED_ITERATION_MARK
specifier|private
specifier|static
specifier|final
name|char
name|HIRAGANA_VOICED_ITERATION_MARK
init|=
literal|'\u309e'
decl_stmt|;
comment|// ã
DECL|field|KATAKANA_ITERATION_MARK
specifier|private
specifier|static
specifier|final
name|char
name|KATAKANA_ITERATION_MARK
init|=
literal|'\u30fd'
decl_stmt|;
comment|// ã½
DECL|field|KATAKANA_VOICED_ITERATION_MARK
specifier|private
specifier|static
specifier|final
name|char
name|KATAKANA_VOICED_ITERATION_MARK
init|=
literal|'\u30fe'
decl_stmt|;
comment|// ã¾
DECL|field|FULL_STOP_PUNCTUATION
specifier|private
specifier|static
specifier|final
name|char
name|FULL_STOP_PUNCTUATION
init|=
literal|'\u3002'
decl_stmt|;
comment|// ã
comment|// Hiragana to dakuten map (lookup using code point - 0x30abï¼ãï¼*/
DECL|field|h2d
specifier|private
specifier|static
name|char
index|[]
name|h2d
init|=
operator|new
name|char
index|[
literal|50
index|]
decl_stmt|;
comment|// Katakana to dakuten map (lookup using code point - 0x30abï¼ã«
DECL|field|k2d
specifier|private
specifier|static
name|char
index|[]
name|k2d
init|=
operator|new
name|char
index|[
literal|50
index|]
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
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
init|=
literal|0
decl_stmt|;
DECL|field|iterationMarksSpanSize
specifier|private
name|int
name|iterationMarksSpanSize
init|=
literal|0
decl_stmt|;
DECL|field|iterationMarkSpanEndPosition
specifier|private
name|int
name|iterationMarkSpanEndPosition
init|=
literal|0
decl_stmt|;
DECL|field|normalizeKanji
specifier|private
name|boolean
name|normalizeKanji
decl_stmt|;
DECL|field|normalizeKana
specifier|private
name|boolean
name|normalizeKana
decl_stmt|;
static|static
block|{
comment|// Hiragana dakuten map
name|h2d
index|[
literal|0
index|]
operator|=
literal|'\u304c'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|1
index|]
operator|=
literal|'\u304c'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|2
index|]
operator|=
literal|'\u304e'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|3
index|]
operator|=
literal|'\u304e'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|4
index|]
operator|=
literal|'\u3050'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|5
index|]
operator|=
literal|'\u3050'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|6
index|]
operator|=
literal|'\u3052'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|7
index|]
operator|=
literal|'\u3052'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|8
index|]
operator|=
literal|'\u3054'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|9
index|]
operator|=
literal|'\u3054'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|10
index|]
operator|=
literal|'\u3056'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|11
index|]
operator|=
literal|'\u3056'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|12
index|]
operator|=
literal|'\u3058'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|13
index|]
operator|=
literal|'\u3058'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|14
index|]
operator|=
literal|'\u305a'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|15
index|]
operator|=
literal|'\u305a'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|16
index|]
operator|=
literal|'\u305c'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|17
index|]
operator|=
literal|'\u305c'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|18
index|]
operator|=
literal|'\u305e'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|19
index|]
operator|=
literal|'\u305e'
expr_stmt|;
comment|// ã => ã
name|h2d
index|[
literal|20
index|]
operator|=
literal|'\u3060'
expr_stmt|;
comment|// ã => ã 
name|h2d
index|[
literal|21
index|]
operator|=
literal|'\u3060'
expr_stmt|;
comment|// ã  => ã 
name|h2d
index|[
literal|22
index|]
operator|=
literal|'\u3062'
expr_stmt|;
comment|// ã¡ => ã¢
name|h2d
index|[
literal|23
index|]
operator|=
literal|'\u3062'
expr_stmt|;
comment|// ã¢ => ã¢
name|h2d
index|[
literal|24
index|]
operator|=
literal|'\u3063'
expr_stmt|;
name|h2d
index|[
literal|25
index|]
operator|=
literal|'\u3065'
expr_stmt|;
comment|// ã¤ => ã¥
name|h2d
index|[
literal|26
index|]
operator|=
literal|'\u3065'
expr_stmt|;
comment|// ã¥ => ã¥
name|h2d
index|[
literal|27
index|]
operator|=
literal|'\u3067'
expr_stmt|;
comment|// ã¦ => ã§
name|h2d
index|[
literal|28
index|]
operator|=
literal|'\u3067'
expr_stmt|;
comment|// ã§ => ã§
name|h2d
index|[
literal|29
index|]
operator|=
literal|'\u3069'
expr_stmt|;
comment|// ã¨ => ã©
name|h2d
index|[
literal|30
index|]
operator|=
literal|'\u3069'
expr_stmt|;
comment|// ã© => ã©
name|h2d
index|[
literal|31
index|]
operator|=
literal|'\u306a'
expr_stmt|;
name|h2d
index|[
literal|32
index|]
operator|=
literal|'\u306b'
expr_stmt|;
name|h2d
index|[
literal|33
index|]
operator|=
literal|'\u306c'
expr_stmt|;
name|h2d
index|[
literal|34
index|]
operator|=
literal|'\u306d'
expr_stmt|;
name|h2d
index|[
literal|35
index|]
operator|=
literal|'\u306e'
expr_stmt|;
name|h2d
index|[
literal|36
index|]
operator|=
literal|'\u3070'
expr_stmt|;
comment|// ã¯ => ã°
name|h2d
index|[
literal|37
index|]
operator|=
literal|'\u3070'
expr_stmt|;
comment|// ã° => ã°
name|h2d
index|[
literal|38
index|]
operator|=
literal|'\u3071'
expr_stmt|;
name|h2d
index|[
literal|39
index|]
operator|=
literal|'\u3073'
expr_stmt|;
comment|// ã² => ã³
name|h2d
index|[
literal|40
index|]
operator|=
literal|'\u3073'
expr_stmt|;
comment|// ã³ => ã³
name|h2d
index|[
literal|41
index|]
operator|=
literal|'\u3074'
expr_stmt|;
name|h2d
index|[
literal|42
index|]
operator|=
literal|'\u3076'
expr_stmt|;
comment|// ãµ => ã¶
name|h2d
index|[
literal|43
index|]
operator|=
literal|'\u3076'
expr_stmt|;
comment|// ã¶ => ã¶
name|h2d
index|[
literal|44
index|]
operator|=
literal|'\u3077'
expr_stmt|;
name|h2d
index|[
literal|45
index|]
operator|=
literal|'\u3079'
expr_stmt|;
comment|// ã¸ => ã¹
name|h2d
index|[
literal|46
index|]
operator|=
literal|'\u3079'
expr_stmt|;
comment|// ã¹ => ã¹
name|h2d
index|[
literal|47
index|]
operator|=
literal|'\u307a'
expr_stmt|;
name|h2d
index|[
literal|48
index|]
operator|=
literal|'\u307c'
expr_stmt|;
comment|// ã» => ã¼
name|h2d
index|[
literal|49
index|]
operator|=
literal|'\u307c'
expr_stmt|;
comment|// ã¼ => ã¼
comment|// Make katakana dakuten map from hiragana map
name|char
name|codePointDifference
init|=
literal|'\u30ab'
operator|-
literal|'\u304b'
decl_stmt|;
comment|// ã« - ã
assert|assert
name|h2d
operator|.
name|length
operator|==
name|k2d
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|k2d
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|k2d
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|h2d
index|[
name|i
index|]
operator|+
name|codePointDifference
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Constructor. Normalizes both kanji and kana iteration marks by default.    *    * @param input char stream    */
DECL|method|JapaneseIterationMarkCharFilter
specifier|public
name|JapaneseIterationMarkCharFilter
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|NORMALIZE_KANJI_DEFAULT
argument_list|,
name|NORMALIZE_KANA_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    *    * @param input          char stream    * @param normalizeKanji indicates whether kanji iteration marks should be normalized    * @param normalizeKana indicates whether kana iteration marks should be normalized    */
DECL|method|JapaneseIterationMarkCharFilter
specifier|public
name|JapaneseIterationMarkCharFilter
parameter_list|(
name|Reader
name|input
parameter_list|,
name|boolean
name|normalizeKanji
parameter_list|,
name|boolean
name|normalizeKana
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|normalizeKanji
operator|=
name|normalizeKanji
expr_stmt|;
name|this
operator|.
name|normalizeKana
operator|=
name|normalizeKana
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|read
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|offset
operator|+
name|length
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
block|{
break|break;
block|}
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
name|read
operator|++
expr_stmt|;
block|}
return|return
name|read
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|read
return|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|int
name|ic
init|=
name|buffer
operator|.
name|get
argument_list|(
name|bufferPosition
argument_list|)
decl_stmt|;
comment|// End of input
if|if
condition|(
name|ic
operator|==
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|freeBefore
argument_list|(
name|bufferPosition
argument_list|)
expr_stmt|;
return|return
name|ic
return|;
block|}
name|char
name|c
init|=
operator|(
name|char
operator|)
name|ic
decl_stmt|;
comment|// Skip surrogate pair characters
if|if
condition|(
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|c
argument_list|)
operator|||
name|Character
operator|.
name|isLowSurrogate
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|iterationMarkSpanEndPosition
operator|=
name|bufferPosition
operator|+
literal|1
expr_stmt|;
block|}
comment|// Free rolling buffer on full stop
if|if
condition|(
name|c
operator|==
name|FULL_STOP_PUNCTUATION
condition|)
block|{
name|buffer
operator|.
name|freeBefore
argument_list|(
name|bufferPosition
argument_list|)
expr_stmt|;
name|iterationMarkSpanEndPosition
operator|=
name|bufferPosition
operator|+
literal|1
expr_stmt|;
block|}
comment|// Normalize iteration mark
if|if
condition|(
name|isIterationMark
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|c
operator|=
name|normalizeIterationMark
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|bufferPosition
operator|++
expr_stmt|;
return|return
name|c
return|;
block|}
comment|/**    * Normalizes the iteration mark character c    *    * @param c iteration mark character to normalize    * @return normalized iteration mark    * @throws IOException If there is a low-level I/O error.    */
DECL|method|normalizeIterationMark
specifier|private
name|char
name|normalizeIterationMark
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Case 1: Inside an iteration mark span
if|if
condition|(
name|bufferPosition
operator|<
name|iterationMarkSpanEndPosition
condition|)
block|{
return|return
name|normalize
argument_list|(
name|sourceCharacter
argument_list|(
name|bufferPosition
argument_list|,
name|iterationMarksSpanSize
argument_list|)
argument_list|,
name|c
argument_list|)
return|;
block|}
comment|// Case 2: New iteration mark spans starts where the previous one ended, which is illegal
if|if
condition|(
name|bufferPosition
operator|==
name|iterationMarkSpanEndPosition
condition|)
block|{
comment|// Emit the illegal iteration mark and increase end position to indicate that we can't
comment|// start a new span on the next position either
name|iterationMarkSpanEndPosition
operator|++
expr_stmt|;
return|return
name|c
return|;
block|}
comment|// Case 3: New iteration mark span
name|iterationMarksSpanSize
operator|=
name|nextIterationMarkSpanSize
argument_list|()
expr_stmt|;
name|iterationMarkSpanEndPosition
operator|=
name|bufferPosition
operator|+
name|iterationMarksSpanSize
expr_stmt|;
return|return
name|normalize
argument_list|(
name|sourceCharacter
argument_list|(
name|bufferPosition
argument_list|,
name|iterationMarksSpanSize
argument_list|)
argument_list|,
name|c
argument_list|)
return|;
block|}
comment|/**    * Finds the number of subsequent next iteration marks    *    * @return number of iteration marks starting at the current buffer position    * @throws IOException If there is a low-level I/O error.    */
DECL|method|nextIterationMarkSpanSize
specifier|private
name|int
name|nextIterationMarkSpanSize
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|spanSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bufferPosition
init|;
name|buffer
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
operator|-
literal|1
operator|&&
name|isIterationMark
argument_list|(
call|(
name|char
call|)
argument_list|(
name|buffer
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|spanSize
operator|++
expr_stmt|;
block|}
comment|// Restrict span size so that we don't go past the previous end position
if|if
condition|(
name|bufferPosition
operator|-
name|spanSize
operator|<
name|iterationMarkSpanEndPosition
condition|)
block|{
name|spanSize
operator|=
name|bufferPosition
operator|-
name|iterationMarkSpanEndPosition
expr_stmt|;
block|}
return|return
name|spanSize
return|;
block|}
comment|/**    * Returns the source character for a given position and iteration mark span size    *    * @param position buffer position (should not exceed bufferPosition)    * @param spanSize iteration mark span size    * @return source character    * @throws IOException If there is a low-level I/O error.    */
DECL|method|sourceCharacter
specifier|private
name|char
name|sourceCharacter
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|spanSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|char
operator|)
name|buffer
operator|.
name|get
argument_list|(
name|position
operator|-
name|spanSize
argument_list|)
return|;
block|}
comment|/**    * Normalize a character    *    * @param c character to normalize    * @param m repetition mark referring to c    * @return normalized character - return c on illegal iteration marks    */
DECL|method|normalize
specifier|private
name|char
name|normalize
parameter_list|(
name|char
name|c
parameter_list|,
name|char
name|m
parameter_list|)
block|{
if|if
condition|(
name|isHiraganaIterationMark
argument_list|(
name|m
argument_list|)
condition|)
block|{
return|return
name|normalizedHiragana
argument_list|(
name|c
argument_list|,
name|m
argument_list|)
return|;
block|}
if|if
condition|(
name|isKatakanaIterationMark
argument_list|(
name|m
argument_list|)
condition|)
block|{
return|return
name|normalizedKatakana
argument_list|(
name|c
argument_list|,
name|m
argument_list|)
return|;
block|}
return|return
name|c
return|;
comment|// If m is not kana and we are to normalize it, we assume it is kanji and simply return it
block|}
comment|/**    * Normalize hiragana character    *    * @param c hiragana character    * @param m repetition mark referring to c    * @return normalized character - return c on illegal iteration marks    */
DECL|method|normalizedHiragana
specifier|private
name|char
name|normalizedHiragana
parameter_list|(
name|char
name|c
parameter_list|,
name|char
name|m
parameter_list|)
block|{
switch|switch
condition|(
name|m
condition|)
block|{
case|case
name|HIRAGANA_ITERATION_MARK
case|:
return|return
name|isHiraganaDakuten
argument_list|(
name|c
argument_list|)
condition|?
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|1
argument_list|)
else|:
name|c
return|;
case|case
name|HIRAGANA_VOICED_ITERATION_MARK
case|:
return|return
name|lookupHiraganaDakuten
argument_list|(
name|c
argument_list|)
return|;
default|default:
return|return
name|c
return|;
block|}
block|}
comment|/**    * Normalize katakana character    *    * @param c katakana character    * @param m repetition mark referring to c    * @return normalized character - return c on illegal iteration marks    */
DECL|method|normalizedKatakana
specifier|private
name|char
name|normalizedKatakana
parameter_list|(
name|char
name|c
parameter_list|,
name|char
name|m
parameter_list|)
block|{
switch|switch
condition|(
name|m
condition|)
block|{
case|case
name|KATAKANA_ITERATION_MARK
case|:
return|return
name|isKatakanaDakuten
argument_list|(
name|c
argument_list|)
condition|?
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|1
argument_list|)
else|:
name|c
return|;
case|case
name|KATAKANA_VOICED_ITERATION_MARK
case|:
return|return
name|lookupKatakanaDakuten
argument_list|(
name|c
argument_list|)
return|;
default|default:
return|return
name|c
return|;
block|}
block|}
comment|/**    * Iteration mark character predicate    *    * @param c character to test    * @return true if c is an iteration mark character.  Otherwise false.    */
DECL|method|isIterationMark
specifier|private
name|boolean
name|isIterationMark
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|isKanjiIterationMark
argument_list|(
name|c
argument_list|)
operator|||
name|isHiraganaIterationMark
argument_list|(
name|c
argument_list|)
operator|||
name|isKatakanaIterationMark
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|/**    * Hiragana iteration mark character predicate    *    * @param c character to test    * @return true if c is a hiragana iteration mark character.  Otherwise false.    */
DECL|method|isHiraganaIterationMark
specifier|private
name|boolean
name|isHiraganaIterationMark
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|normalizeKana
condition|)
block|{
return|return
name|c
operator|==
name|HIRAGANA_ITERATION_MARK
operator|||
name|c
operator|==
name|HIRAGANA_VOICED_ITERATION_MARK
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Katakana iteration mark character predicate    *    * @param c character to test    * @return true if c is a katakana iteration mark character.  Otherwise false.    */
DECL|method|isKatakanaIterationMark
specifier|private
name|boolean
name|isKatakanaIterationMark
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|normalizeKana
condition|)
block|{
return|return
name|c
operator|==
name|KATAKANA_ITERATION_MARK
operator|||
name|c
operator|==
name|KATAKANA_VOICED_ITERATION_MARK
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Kanji iteration mark character predicate    *    * @param c character to test    * @return true if c is a kanji iteration mark character.  Otherwise false.    */
DECL|method|isKanjiIterationMark
specifier|private
name|boolean
name|isKanjiIterationMark
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|normalizeKanji
condition|)
block|{
return|return
name|c
operator|==
name|KANJI_ITERATION_MARK
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Look up hiragana dakuten    *    * @param c character to look up    * @return hiragana dakuten variant of c or c itself if no dakuten variant exists    */
DECL|method|lookupHiraganaDakuten
specifier|private
name|char
name|lookupHiraganaDakuten
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|lookup
argument_list|(
name|c
argument_list|,
name|h2d
argument_list|,
literal|'\u304b'
argument_list|)
return|;
comment|// Code point is for ã
block|}
comment|/**    * Look up katakana dakuten. Only full-width katakana are supported.    *    * @param c character to look up    * @return katakana dakuten variant of c or c itself if no dakuten variant exists    */
DECL|method|lookupKatakanaDakuten
specifier|private
name|char
name|lookupKatakanaDakuten
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|lookup
argument_list|(
name|c
argument_list|,
name|k2d
argument_list|,
literal|'\u30ab'
argument_list|)
return|;
comment|// Code point is for ã«
block|}
comment|/**    * Hiragana dakuten predicate    *    * @param c character to check    * @return true if c is a hiragana dakuten and otherwise false    */
DECL|method|isHiraganaDakuten
specifier|private
name|boolean
name|isHiraganaDakuten
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|inside
argument_list|(
name|c
argument_list|,
name|h2d
argument_list|,
literal|'\u304b'
argument_list|)
operator|&&
name|c
operator|==
name|lookupHiraganaDakuten
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|/**    * Katakana dakuten predicate    *    * @param c character to check    * @return true if c is a hiragana dakuten and otherwise false    */
DECL|method|isKatakanaDakuten
specifier|private
name|boolean
name|isKatakanaDakuten
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|inside
argument_list|(
name|c
argument_list|,
name|k2d
argument_list|,
literal|'\u30ab'
argument_list|)
operator|&&
name|c
operator|==
name|lookupKatakanaDakuten
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|/**    * Looks up a character in dakuten map and returns the dakuten variant if it exists.    * Otherwise return the character being looked up itself    *    * @param c      character to look up    * @param map    dakuten map    * @param offset code point offset from c    * @return mapped character or c if no mapping exists    */
DECL|method|lookup
specifier|private
name|char
name|lookup
parameter_list|(
name|char
name|c
parameter_list|,
name|char
index|[]
name|map
parameter_list|,
name|char
name|offset
parameter_list|)
block|{
if|if
condition|(
operator|!
name|inside
argument_list|(
name|c
argument_list|,
name|map
argument_list|,
name|offset
argument_list|)
condition|)
block|{
return|return
name|c
return|;
block|}
else|else
block|{
return|return
name|map
index|[
name|c
operator|-
name|offset
index|]
return|;
block|}
block|}
comment|/**    * Predicate indicating if the lookup character is within dakuten map range    *    * @param c      character to look up    * @param map    dakuten map    * @param offset code point offset from c    * @return true if c is mapped by map and otherwise false    */
DECL|method|inside
specifier|private
name|boolean
name|inside
parameter_list|(
name|char
name|c
parameter_list|,
name|char
index|[]
name|map
parameter_list|,
name|char
name|offset
parameter_list|)
block|{
return|return
name|c
operator|>=
name|offset
operator|&&
name|c
operator|<
name|offset
operator|+
name|map
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|correct
specifier|protected
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
name|currentOff
return|;
comment|// this filter doesn't change the length of strings
block|}
block|}
end_class

end_unit

