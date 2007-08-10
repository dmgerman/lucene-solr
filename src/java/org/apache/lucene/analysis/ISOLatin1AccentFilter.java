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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A filter that replaces accented characters in the ISO Latin 1 character set   * (ISO-8859-1) by their unaccented equivalent. The case will not be altered.  *<p>  * For instance, '&agrave;' will be replaced by 'a'.  *<p>  */
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
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
name|Token
name|result
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|result
operator|=
name|input
operator|.
name|next
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|outputPos
operator|=
literal|0
expr_stmt|;
name|removeAccents
argument_list|(
name|result
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|result
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|result
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
return|return
name|result
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
DECL|method|addChar
specifier|private
specifier|final
name|void
name|addChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|outputPos
operator|==
name|output
operator|.
name|length
condition|)
block|{
name|char
index|[]
name|newArray
init|=
operator|new
name|char
index|[
literal|2
operator|*
name|output
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|=
name|newArray
expr_stmt|;
block|}
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
name|c
expr_stmt|;
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
switch|switch
condition|(
name|input
index|[
name|pos
index|]
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
name|addChar
argument_list|(
literal|'A'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00C6'
case|:
comment|// Ã
name|addChar
argument_list|(
literal|'A'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'E'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00C7'
case|:
comment|// Ã
name|addChar
argument_list|(
literal|'C'
argument_list|)
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
name|addChar
argument_list|(
literal|'E'
argument_list|)
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
name|addChar
argument_list|(
literal|'I'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00D0'
case|:
comment|// Ã
name|addChar
argument_list|(
literal|'D'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00D1'
case|:
comment|// Ã
name|addChar
argument_list|(
literal|'N'
argument_list|)
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
name|addChar
argument_list|(
literal|'O'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u0152'
case|:
comment|// Å
name|addChar
argument_list|(
literal|'O'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'E'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00DE'
case|:
comment|// Ã
name|addChar
argument_list|(
literal|'T'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'H'
argument_list|)
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
name|addChar
argument_list|(
literal|'U'
argument_list|)
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
name|addChar
argument_list|(
literal|'Y'
argument_list|)
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
name|addChar
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00E6'
case|:
comment|// Ã¦
name|addChar
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'e'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00E7'
case|:
comment|// Ã§
name|addChar
argument_list|(
literal|'c'
argument_list|)
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
name|addChar
argument_list|(
literal|'e'
argument_list|)
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
name|addChar
argument_list|(
literal|'i'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00F0'
case|:
comment|// Ã°
name|addChar
argument_list|(
literal|'d'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00F1'
case|:
comment|// Ã±
name|addChar
argument_list|(
literal|'n'
argument_list|)
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
name|addChar
argument_list|(
literal|'o'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u0153'
case|:
comment|// Å
name|addChar
argument_list|(
literal|'o'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'e'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00DF'
case|:
comment|// Ã
name|addChar
argument_list|(
literal|'s'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'s'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00FE'
case|:
comment|// Ã¾
name|addChar
argument_list|(
literal|'t'
argument_list|)
expr_stmt|;
name|addChar
argument_list|(
literal|'h'
argument_list|)
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
name|addChar
argument_list|(
literal|'u'
argument_list|)
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
name|addChar
argument_list|(
literal|'y'
argument_list|)
expr_stmt|;
break|break;
default|default :
name|addChar
argument_list|(
name|input
index|[
name|pos
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

