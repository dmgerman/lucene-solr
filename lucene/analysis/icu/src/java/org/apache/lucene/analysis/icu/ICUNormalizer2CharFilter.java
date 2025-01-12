begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
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
name|Objects
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
name|charfilter
operator|.
name|BaseCharFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Normalizer2
import|;
end_import

begin_comment
comment|/**  * Normalize token text with ICU's {@link Normalizer2}.  */
end_comment

begin_class
DECL|class|ICUNormalizer2CharFilter
specifier|public
specifier|final
class|class
name|ICUNormalizer2CharFilter
extends|extends
name|BaseCharFilter
block|{
DECL|field|normalizer
specifier|private
specifier|final
name|Normalizer2
name|normalizer
decl_stmt|;
DECL|field|inputBuffer
specifier|private
specifier|final
name|StringBuilder
name|inputBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|resultBuffer
specifier|private
specifier|final
name|StringBuilder
name|resultBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|inputFinished
specifier|private
name|boolean
name|inputFinished
decl_stmt|;
DECL|field|afterQuickCheckYes
specifier|private
name|boolean
name|afterQuickCheckYes
decl_stmt|;
DECL|field|checkedInputBoundary
specifier|private
name|int
name|checkedInputBoundary
decl_stmt|;
DECL|field|charCount
specifier|private
name|int
name|charCount
decl_stmt|;
comment|/**    * Create a new Normalizer2CharFilter that combines NFKC normalization, Case    * Folding, and removes Default Ignorables (NFKC_Casefold)    */
DECL|method|ICUNormalizer2CharFilter
specifier|public
name|ICUNormalizer2CharFilter
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new Normalizer2CharFilter with the specified Normalizer2    * @param in text    * @param normalizer normalizer to use    */
DECL|method|ICUNormalizer2CharFilter
specifier|public
name|ICUNormalizer2CharFilter
parameter_list|(
name|Reader
name|in
parameter_list|,
name|Normalizer2
name|normalizer
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|normalizer
argument_list|,
literal|128
argument_list|)
expr_stmt|;
block|}
comment|// for testing ONLY
DECL|method|ICUNormalizer2CharFilter
name|ICUNormalizer2CharFilter
parameter_list|(
name|Reader
name|in
parameter_list|,
name|Normalizer2
name|normalizer
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|normalizer
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|normalizer
argument_list|)
expr_stmt|;
name|this
operator|.
name|tmpBuffer
operator|=
operator|new
name|char
index|[
name|bufferSize
index|]
expr_stmt|;
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
if|if
condition|(
name|off
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"off< 0"
argument_list|)
throw|;
if|if
condition|(
name|off
operator|>=
name|cbuf
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"off>= cbuf.length"
argument_list|)
throw|;
if|if
condition|(
name|len
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"len<= 0"
argument_list|)
throw|;
while|while
condition|(
operator|!
name|inputFinished
operator|||
name|inputBuffer
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|||
name|resultBuffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|retLen
decl_stmt|;
if|if
condition|(
name|resultBuffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|retLen
operator|=
name|outputFromResultBuffer
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|retLen
operator|>
literal|0
condition|)
block|{
return|return
name|retLen
return|;
block|}
block|}
name|int
name|resLen
init|=
name|readAndNormalizeFromInput
argument_list|()
decl_stmt|;
if|if
condition|(
name|resLen
operator|>
literal|0
condition|)
block|{
name|retLen
operator|=
name|outputFromResultBuffer
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|retLen
operator|>
literal|0
condition|)
block|{
return|return
name|retLen
return|;
block|}
block|}
name|readInputToBuffer
argument_list|()
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|field|tmpBuffer
specifier|private
specifier|final
name|char
index|[]
name|tmpBuffer
decl_stmt|;
DECL|method|readInputToBuffer
specifier|private
name|int
name|readInputToBuffer
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|input
operator|.
name|read
argument_list|(
name|tmpBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
block|{
name|inputFinished
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
name|inputBuffer
operator|.
name|append
argument_list|(
name|tmpBuffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// if checkedInputBoundary was at the end of a buffer, we need to check that char again
name|checkedInputBoundary
operator|=
name|Math
operator|.
name|max
argument_list|(
name|checkedInputBoundary
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// this loop depends on 'isInert' (changes under normalization) but looks only at characters.
comment|// so we treat all surrogates as non-inert for simplicity
if|if
condition|(
name|normalizer
operator|.
name|isInert
argument_list|(
name|tmpBuffer
index|[
name|len
operator|-
literal|1
index|]
argument_list|)
operator|&&
operator|!
name|Character
operator|.
name|isSurrogate
argument_list|(
name|tmpBuffer
index|[
name|len
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
return|return
name|len
return|;
block|}
else|else
return|return
name|len
operator|+
name|readInputToBuffer
argument_list|()
return|;
block|}
DECL|method|readAndNormalizeFromInput
specifier|private
name|int
name|readAndNormalizeFromInput
parameter_list|()
block|{
if|if
condition|(
name|inputBuffer
operator|.
name|length
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|afterQuickCheckYes
operator|=
literal|false
expr_stmt|;
return|return
literal|0
return|;
block|}
if|if
condition|(
operator|!
name|afterQuickCheckYes
condition|)
block|{
name|int
name|resLen
init|=
name|readFromInputWhileSpanQuickCheckYes
argument_list|()
decl_stmt|;
name|afterQuickCheckYes
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|resLen
operator|>
literal|0
condition|)
return|return
name|resLen
return|;
block|}
name|int
name|resLen
init|=
name|readFromIoNormalizeUptoBoundary
argument_list|()
decl_stmt|;
if|if
condition|(
name|resLen
operator|>
literal|0
condition|)
block|{
name|afterQuickCheckYes
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|resLen
return|;
block|}
DECL|method|readFromInputWhileSpanQuickCheckYes
specifier|private
name|int
name|readFromInputWhileSpanQuickCheckYes
parameter_list|()
block|{
name|int
name|end
init|=
name|normalizer
operator|.
name|spanQuickCheckYes
argument_list|(
name|inputBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|>
literal|0
condition|)
block|{
name|resultBuffer
operator|.
name|append
argument_list|(
name|inputBuffer
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|inputBuffer
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|checkedInputBoundary
operator|=
name|Math
operator|.
name|max
argument_list|(
name|checkedInputBoundary
operator|-
name|end
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|charCount
operator|+=
name|end
expr_stmt|;
block|}
return|return
name|end
return|;
block|}
DECL|method|readFromIoNormalizeUptoBoundary
specifier|private
name|int
name|readFromIoNormalizeUptoBoundary
parameter_list|()
block|{
comment|// if there's no buffer to normalize, return 0
if|if
condition|(
name|inputBuffer
operator|.
name|length
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|boolean
name|foundBoundary
init|=
literal|false
decl_stmt|;
specifier|final
name|int
name|bufLen
init|=
name|inputBuffer
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|checkedInputBoundary
operator|<=
name|bufLen
operator|-
literal|1
condition|)
block|{
name|int
name|charLen
init|=
name|Character
operator|.
name|charCount
argument_list|(
name|inputBuffer
operator|.
name|codePointAt
argument_list|(
name|checkedInputBoundary
argument_list|)
argument_list|)
decl_stmt|;
name|checkedInputBoundary
operator|+=
name|charLen
expr_stmt|;
if|if
condition|(
name|checkedInputBoundary
operator|<
name|bufLen
operator|&&
name|normalizer
operator|.
name|hasBoundaryBefore
argument_list|(
name|inputBuffer
operator|.
name|codePointAt
argument_list|(
name|checkedInputBoundary
argument_list|)
argument_list|)
condition|)
block|{
name|foundBoundary
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|foundBoundary
operator|&&
name|checkedInputBoundary
operator|>=
name|bufLen
operator|&&
name|inputFinished
condition|)
block|{
name|foundBoundary
operator|=
literal|true
expr_stmt|;
name|checkedInputBoundary
operator|=
name|bufLen
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundBoundary
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|normalizeInputUpto
argument_list|(
name|checkedInputBoundary
argument_list|)
return|;
block|}
DECL|method|normalizeInputUpto
specifier|private
name|int
name|normalizeInputUpto
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
specifier|final
name|int
name|destOrigLen
init|=
name|resultBuffer
operator|.
name|length
argument_list|()
decl_stmt|;
name|normalizer
operator|.
name|normalizeSecondAndAppend
argument_list|(
name|resultBuffer
argument_list|,
name|inputBuffer
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|inputBuffer
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|checkedInputBoundary
operator|=
name|Math
operator|.
name|max
argument_list|(
name|checkedInputBoundary
operator|-
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|resultLength
init|=
name|resultBuffer
operator|.
name|length
argument_list|()
operator|-
name|destOrigLen
decl_stmt|;
name|recordOffsetDiff
argument_list|(
name|length
argument_list|,
name|resultLength
argument_list|)
expr_stmt|;
return|return
name|resultLength
return|;
block|}
DECL|method|recordOffsetDiff
specifier|private
name|void
name|recordOffsetDiff
parameter_list|(
name|int
name|inputLength
parameter_list|,
name|int
name|outputLength
parameter_list|)
block|{
if|if
condition|(
name|inputLength
operator|==
name|outputLength
condition|)
block|{
name|charCount
operator|+=
name|outputLength
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|diff
init|=
name|inputLength
operator|-
name|outputLength
decl_stmt|;
specifier|final
name|int
name|cumuDiff
init|=
name|getLastCumulativeDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|<
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
operator|-
name|diff
condition|;
operator|++
name|i
control|)
block|{
name|addOffCorrectMap
argument_list|(
name|charCount
operator|+
name|i
argument_list|,
name|cumuDiff
operator|-
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|addOffCorrectMap
argument_list|(
name|charCount
operator|+
name|outputLength
argument_list|,
name|cumuDiff
operator|+
name|diff
argument_list|)
expr_stmt|;
block|}
name|charCount
operator|+=
name|outputLength
expr_stmt|;
block|}
DECL|method|outputFromResultBuffer
specifier|private
name|int
name|outputFromResultBuffer
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|resultBuffer
operator|.
name|length
argument_list|()
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|resultBuffer
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|cbuf
argument_list|,
name|begin
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|resultBuffer
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

