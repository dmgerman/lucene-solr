begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|tokenattributes
operator|.
name|TermAttribute
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A filter that replaces accented characters in the ISO Latin 1 character set   * (ISO-8859-1) by their unaccented equivalent. The case will not be altered.  *<p>  * For instance, '&agrave;' will be replaced by 'a'.  *<p>  *   * @deprecated in favor of {@link ASCIIFoldingFilter} which covers a superset   * of Latin 1. This class will be removed in Lucene 3.0.  */
end_comment

begin_class
DECL|class|ISOLatin1AccentFilter
specifier|public
class|class
name|ISOLatin1AccentFilter
extends|extends
name|TokenFilter
block|{
DECL|method|ISOLatin1AccentFilter
specifier|public
name|ISOLatin1AccentFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|output
specifier|private
name|char
index|[]
name|output
init|=
operator|new
name|char
index|[
literal|256
index|]
decl_stmt|;
DECL|field|outputPos
specifier|private
name|int
name|outputPos
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|termAtt
operator|.
name|termLength
argument_list|()
decl_stmt|;
comment|// If no characters actually require rewriting then we
comment|// just return token as-is:
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
specifier|final
name|char
name|c
init|=
name|buffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|'\u00c0'
operator|&&
name|c
operator|<=
literal|'\uFB06'
condition|)
block|{
name|removeAccents
argument_list|(
name|buffer
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|outputPos
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/** @deprecated */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|!=
literal|null
condition|)
block|{
specifier|final
name|char
index|[]
name|buffer
init|=
name|nextToken
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|nextToken
operator|.
name|termLength
argument_list|()
decl_stmt|;
comment|// If no characters actually require rewriting then we
comment|// just return token as-is:
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
specifier|final
name|char
name|c
init|=
name|buffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|'\u00c0'
operator|&&
name|c
operator|<=
literal|'\uFB06'
condition|)
block|{
name|removeAccents
argument_list|(
name|buffer
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|nextToken
operator|.
name|setTermBuffer
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|outputPos
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|nextToken
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
comment|/**    * To replace accented characters in a String by unaccented equivalents.    */
DECL|method|removeAccents
specifier|public
specifier|final
name|void
name|removeAccents
parameter_list|(
name|char
index|[]
name|input
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|// Worst-case length required:
specifier|final
name|int
name|maxSizeNeeded
init|=
literal|2
operator|*
name|length
decl_stmt|;
name|int
name|size
init|=
name|output
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|size
operator|<
name|maxSizeNeeded
condition|)
name|size
operator|*=
literal|2
expr_stmt|;
if|if
condition|(
name|size
operator|!=
name|output
operator|.
name|length
condition|)
name|output
operator|=
operator|new
name|char
index|[
name|size
index|]
expr_stmt|;
name|outputPos
operator|=
literal|0
expr_stmt|;
name|int
name|pos
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
name|length
condition|;
name|i
operator|++
operator|,
name|pos
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|input
index|[
name|pos
index|]
decl_stmt|;
comment|// Quick test: if it's not in range then just keep
comment|// current character
if|if
condition|(
name|c
argument_list|<
literal|'\u00c0'
operator|||
name|c
argument_list|>
literal|'\uFB06'
condition|)
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
name|c
expr_stmt|;
else|else
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\u00C0'
case|:
comment|// Ã
case|case
literal|'\u00C1'
case|:
comment|// Ã
case|case
literal|'\u00C2'
case|:
comment|// Ã
case|case
literal|'\u00C3'
case|:
comment|// Ã
case|case
literal|'\u00C4'
case|:
comment|// Ã
case|case
literal|'\u00C5'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
break|break;
case|case
literal|'\u00C6'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'E'
expr_stmt|;
break|break;
case|case
literal|'\u00C7'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'C'
expr_stmt|;
break|break;
case|case
literal|'\u00C8'
case|:
comment|// Ã
case|case
literal|'\u00C9'
case|:
comment|// Ã
case|case
literal|'\u00CA'
case|:
comment|// Ã
case|case
literal|'\u00CB'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'E'
expr_stmt|;
break|break;
case|case
literal|'\u00CC'
case|:
comment|// Ã
case|case
literal|'\u00CD'
case|:
comment|// Ã
case|case
literal|'\u00CE'
case|:
comment|// Ã
case|case
literal|'\u00CF'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'I'
expr_stmt|;
break|break;
case|case
literal|'\u0132'
case|:
comment|// Ä²
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'I'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'J'
expr_stmt|;
break|break;
case|case
literal|'\u00D0'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'D'
expr_stmt|;
break|break;
case|case
literal|'\u00D1'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'N'
expr_stmt|;
break|break;
case|case
literal|'\u00D2'
case|:
comment|// Ã
case|case
literal|'\u00D3'
case|:
comment|// Ã
case|case
literal|'\u00D4'
case|:
comment|// Ã
case|case
literal|'\u00D5'
case|:
comment|// Ã
case|case
literal|'\u00D6'
case|:
comment|// Ã
case|case
literal|'\u00D8'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
break|break;
case|case
literal|'\u0152'
case|:
comment|// Å
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'E'
expr_stmt|;
break|break;
case|case
literal|'\u00DE'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'T'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'H'
expr_stmt|;
break|break;
case|case
literal|'\u00D9'
case|:
comment|// Ã
case|case
literal|'\u00DA'
case|:
comment|// Ã
case|case
literal|'\u00DB'
case|:
comment|// Ã
case|case
literal|'\u00DC'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'U'
expr_stmt|;
break|break;
case|case
literal|'\u00DD'
case|:
comment|// Ã
case|case
literal|'\u0178'
case|:
comment|// Å¸
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Y'
expr_stmt|;
break|break;
case|case
literal|'\u00E0'
case|:
comment|// Ã 
case|case
literal|'\u00E1'
case|:
comment|// Ã¡
case|case
literal|'\u00E2'
case|:
comment|// Ã¢
case|case
literal|'\u00E3'
case|:
comment|// Ã£
case|case
literal|'\u00E4'
case|:
comment|// Ã¤
case|case
literal|'\u00E5'
case|:
comment|// Ã¥
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'\u00E6'
case|:
comment|// Ã¦
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\u00E7'
case|:
comment|// Ã§
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'c'
expr_stmt|;
break|break;
case|case
literal|'\u00E8'
case|:
comment|// Ã¨
case|case
literal|'\u00E9'
case|:
comment|// Ã©
case|case
literal|'\u00EA'
case|:
comment|// Ãª
case|case
literal|'\u00EB'
case|:
comment|// Ã«
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\u00EC'
case|:
comment|// Ã¬
case|case
literal|'\u00ED'
case|:
comment|// Ã­
case|case
literal|'\u00EE'
case|:
comment|// Ã®
case|case
literal|'\u00EF'
case|:
comment|// Ã¯
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'\u0133'
case|:
comment|// Ä³
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u00F0'
case|:
comment|// Ã°
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'d'
expr_stmt|;
break|break;
case|case
literal|'\u00F1'
case|:
comment|// Ã±
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'n'
expr_stmt|;
break|break;
case|case
literal|'\u00F2'
case|:
comment|// Ã²
case|case
literal|'\u00F3'
case|:
comment|// Ã³
case|case
literal|'\u00F4'
case|:
comment|// Ã´
case|case
literal|'\u00F5'
case|:
comment|// Ãµ
case|case
literal|'\u00F6'
case|:
comment|// Ã¶
case|case
literal|'\u00F8'
case|:
comment|// Ã¸
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'\u0153'
case|:
comment|// Å
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\u00DF'
case|:
comment|// Ã
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
break|break;
case|case
literal|'\u00FE'
case|:
comment|// Ã¾
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'h'
expr_stmt|;
break|break;
case|case
literal|'\u00F9'
case|:
comment|// Ã¹
case|case
literal|'\u00FA'
case|:
comment|// Ãº
case|case
literal|'\u00FB'
case|:
comment|// Ã»
case|case
literal|'\u00FC'
case|:
comment|// Ã¼
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'\u00FD'
case|:
comment|// Ã½
case|case
literal|'\u00FF'
case|:
comment|// Ã¿
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'y'
expr_stmt|;
break|break;
case|case
literal|'\uFB00'
case|:
comment|// ï¬
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
break|break;
case|case
literal|'\uFB01'
case|:
comment|// ï¬
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'\uFB02'
case|:
comment|// ï¬
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
break|break;
comment|// following 2 are commented as they can break the maxSizeNeeded (and doing *3 could be expensive)
comment|//        case '\uFB03': // ï¬
comment|//            output[outputPos++] = 'f';
comment|//            output[outputPos++] = 'f';
comment|//            output[outputPos++] = 'i';
comment|//            break;
comment|//        case '\uFB04': // ï¬
comment|//            output[outputPos++] = 'f';
comment|//            output[outputPos++] = 'f';
comment|//            output[outputPos++] = 'l';
comment|//            break;
case|case
literal|'\uFB05'
case|:
comment|// ï¬
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
break|break;
case|case
literal|'\uFB06'
case|:
comment|// ï¬
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
break|break;
default|default :
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
name|c
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

