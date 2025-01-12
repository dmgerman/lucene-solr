begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 1999-2010, International Business Machines  * Corporation and others.  All Rights Reserved.  *  * Permission is hereby granted, free of charge, to any person obtaining a copy   * of this software and associated documentation files (the "Software"), to deal  * in the Software without restriction, including without limitation the rights   * to use, copy, modify, merge, publish, distribute, and/or sell copies of the   * Software, and to permit persons to whom the Software is furnished to do so,   * provided that the above copyright notice(s) and this permission notice appear   * in all copies of the Software and that both the above copyright notice(s) and  * this permission notice appear in supporting documentation.  *   * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,   * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS.   * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE   * LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR   * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER   * IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT   * OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.  *  * Except as contained in this notice, the name of a copyright holder shall not   * be used in advertising or otherwise to promote the sale, use or other   * dealings in this Software without prior written authorization of the   * copyright holder.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
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
operator|.
name|segmentation
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UCharacter
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
name|lang
operator|.
name|UCharacterEnums
operator|.
name|ECharacterCategory
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
name|lang
operator|.
name|UScript
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
name|UTF16
import|;
end_import

begin_comment
comment|/**  * An iterator that locates ISO 15924 script boundaries in text.   *<p>  * This is not the same as simply looking at the Unicode block, or even the   * Script property. Some characters are 'common' across multiple scripts, and  * some 'inherit' the script value of text surrounding them.  *<p>  * This is similar to ICU (internal-only) UScriptRun, with the following  * differences:  *<ul>  *<li>Doesn't attempt to match paired punctuation. For tokenization purposes, this  * is not necessary. It's also quite expensive.   *<li>Non-spacing marks inherit the script of their base character, following   *  recommendations from UTR #24.  *</ul>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ScriptIterator
specifier|final
class|class
name|ScriptIterator
block|{
DECL|field|text
specifier|private
name|char
name|text
index|[]
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|field|scriptStart
specifier|private
name|int
name|scriptStart
decl_stmt|;
DECL|field|scriptLimit
specifier|private
name|int
name|scriptLimit
decl_stmt|;
DECL|field|scriptCode
specifier|private
name|int
name|scriptCode
decl_stmt|;
DECL|field|combineCJ
specifier|private
specifier|final
name|boolean
name|combineCJ
decl_stmt|;
comment|/**    * @param combineCJ if true: Han,Hiragana,Katakana will all return as {@link UScript#JAPANESE}    */
DECL|method|ScriptIterator
name|ScriptIterator
parameter_list|(
name|boolean
name|combineCJ
parameter_list|)
block|{
name|this
operator|.
name|combineCJ
operator|=
name|combineCJ
expr_stmt|;
block|}
comment|/**    * Get the start of this script run    *     * @return start position of script run    */
DECL|method|getScriptStart
name|int
name|getScriptStart
parameter_list|()
block|{
return|return
name|scriptStart
return|;
block|}
comment|/**    * Get the index of the first character after the end of this script run    *     * @return position of the first character after this script run    */
DECL|method|getScriptLimit
name|int
name|getScriptLimit
parameter_list|()
block|{
return|return
name|scriptLimit
return|;
block|}
comment|/**    * Get the UScript script code for this script run    *     * @return code for the script of the current run    */
DECL|method|getScriptCode
name|int
name|getScriptCode
parameter_list|()
block|{
return|return
name|scriptCode
return|;
block|}
comment|/**    * Iterates to the next script run, returning true if one exists.    *     * @return true if there is another script run, false otherwise.    */
DECL|method|next
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|scriptLimit
operator|>=
name|limit
condition|)
return|return
literal|false
return|;
name|scriptCode
operator|=
name|UScript
operator|.
name|COMMON
expr_stmt|;
name|scriptStart
operator|=
name|scriptLimit
expr_stmt|;
while|while
condition|(
name|index
operator|<
name|limit
condition|)
block|{
specifier|final
name|int
name|ch
init|=
name|UTF16
operator|.
name|charAt
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|limit
argument_list|,
name|index
operator|-
name|start
argument_list|)
decl_stmt|;
specifier|final
name|int
name|sc
init|=
name|getScript
argument_list|(
name|ch
argument_list|)
decl_stmt|;
comment|/*        * From UTR #24: Implementations that determine the boundaries between        * characters of given scripts should never break between a non-spacing        * mark and its base character. Thus for boundary determinations and        * similar sorts of processing, a non-spacing mark â whatever its script        * value â should inherit the script value of its base character.        */
if|if
condition|(
name|isSameScript
argument_list|(
name|scriptCode
argument_list|,
name|sc
argument_list|)
operator|||
name|UCharacter
operator|.
name|getType
argument_list|(
name|ch
argument_list|)
operator|==
name|ECharacterCategory
operator|.
name|NON_SPACING_MARK
condition|)
block|{
name|index
operator|+=
name|UTF16
operator|.
name|getCharCount
argument_list|(
name|ch
argument_list|)
expr_stmt|;
comment|/*          * Inherited or Common becomes the script code of the surrounding text.          */
if|if
condition|(
name|scriptCode
operator|<=
name|UScript
operator|.
name|INHERITED
operator|&&
name|sc
operator|>
name|UScript
operator|.
name|INHERITED
condition|)
block|{
name|scriptCode
operator|=
name|sc
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
name|scriptLimit
operator|=
name|index
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Determine if two scripts are compatible. */
DECL|method|isSameScript
specifier|private
specifier|static
name|boolean
name|isSameScript
parameter_list|(
name|int
name|scriptOne
parameter_list|,
name|int
name|scriptTwo
parameter_list|)
block|{
return|return
name|scriptOne
operator|<=
name|UScript
operator|.
name|INHERITED
operator|||
name|scriptTwo
operator|<=
name|UScript
operator|.
name|INHERITED
operator|||
name|scriptOne
operator|==
name|scriptTwo
return|;
block|}
comment|/**    * Set a new region of text to be examined by this iterator    *     * @param text text buffer to examine    * @param start offset into buffer    * @param length maximum length to examine    */
DECL|method|setText
name|void
name|setText
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|start
operator|+
name|length
expr_stmt|;
name|this
operator|.
name|scriptStart
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|scriptLimit
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|scriptCode
operator|=
name|UScript
operator|.
name|INVALID_CODE
expr_stmt|;
block|}
comment|/** linear fast-path for basic latin case */
DECL|field|basicLatin
specifier|private
specifier|static
specifier|final
name|int
name|basicLatin
index|[]
init|=
operator|new
name|int
index|[
literal|128
index|]
decl_stmt|;
static|static
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
name|basicLatin
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|basicLatin
index|[
name|i
index|]
operator|=
name|UScript
operator|.
name|getScript
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** fast version of UScript.getScript(). Basic Latin is an array lookup */
DECL|method|getScript
specifier|private
name|int
name|getScript
parameter_list|(
name|int
name|codepoint
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|<=
name|codepoint
operator|&&
name|codepoint
operator|<
name|basicLatin
operator|.
name|length
condition|)
block|{
return|return
name|basicLatin
index|[
name|codepoint
index|]
return|;
block|}
else|else
block|{
name|int
name|script
init|=
name|UScript
operator|.
name|getScript
argument_list|(
name|codepoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineCJ
condition|)
block|{
if|if
condition|(
name|script
operator|==
name|UScript
operator|.
name|HAN
operator|||
name|script
operator|==
name|UScript
operator|.
name|HIRAGANA
operator|||
name|script
operator|==
name|UScript
operator|.
name|KATAKANA
condition|)
block|{
return|return
name|UScript
operator|.
name|JAPANESE
return|;
block|}
elseif|else
if|if
condition|(
name|codepoint
operator|>=
literal|0xFF10
operator|&&
name|codepoint
operator|<=
literal|0xFF19
condition|)
block|{
comment|// when using CJK dictionary breaking, don't let full width numbers go to it, otherwise
comment|// they are treated as punctuation. we currently have no cleaner way to fix this!
return|return
name|UScript
operator|.
name|LATIN
return|;
block|}
else|else
block|{
return|return
name|script
return|;
block|}
block|}
else|else
block|{
return|return
name|script
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

