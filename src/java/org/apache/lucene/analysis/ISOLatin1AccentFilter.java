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
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
specifier|final
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
name|t
operator|.
name|setTermText
argument_list|(
name|removeAccents
argument_list|(
name|t
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
comment|/** 	 * To replace accented characters in a String by unaccented equivalents. 	 */
DECL|method|removeAccents
specifier|public
specifier|final
specifier|static
name|String
name|removeAccents
parameter_list|(
name|String
name|input
parameter_list|)
block|{
specifier|final
name|StringBuffer
name|output
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|input
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|input
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
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
operator|.
name|append
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00C6'
case|:
comment|// Ã
name|output
operator|.
name|append
argument_list|(
literal|"AE"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00C7'
case|:
comment|// Ã
name|output
operator|.
name|append
argument_list|(
literal|"C"
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
name|output
operator|.
name|append
argument_list|(
literal|"E"
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
name|output
operator|.
name|append
argument_list|(
literal|"I"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00D0'
case|:
comment|// Ã
name|output
operator|.
name|append
argument_list|(
literal|"D"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00D1'
case|:
comment|// Ã
name|output
operator|.
name|append
argument_list|(
literal|"N"
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
name|output
operator|.
name|append
argument_list|(
literal|"O"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u0152'
case|:
comment|// Å
name|output
operator|.
name|append
argument_list|(
literal|"OE"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00DE'
case|:
comment|// Ã
name|output
operator|.
name|append
argument_list|(
literal|"TH"
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
name|output
operator|.
name|append
argument_list|(
literal|"U"
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
name|output
operator|.
name|append
argument_list|(
literal|"Y"
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
name|output
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00E6'
case|:
comment|// Ã¦
name|output
operator|.
name|append
argument_list|(
literal|"ae"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00E7'
case|:
comment|// Ã§
name|output
operator|.
name|append
argument_list|(
literal|"c"
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
name|output
operator|.
name|append
argument_list|(
literal|"e"
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
name|output
operator|.
name|append
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00F0'
case|:
comment|// Ã°
name|output
operator|.
name|append
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00F1'
case|:
comment|// Ã±
name|output
operator|.
name|append
argument_list|(
literal|"n"
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
name|output
operator|.
name|append
argument_list|(
literal|"o"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u0153'
case|:
comment|// Å
name|output
operator|.
name|append
argument_list|(
literal|"oe"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00DF'
case|:
comment|// Ã
name|output
operator|.
name|append
argument_list|(
literal|"ss"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\u00FE'
case|:
comment|// Ã¾
name|output
operator|.
name|append
argument_list|(
literal|"th"
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
name|output
operator|.
name|append
argument_list|(
literal|"u"
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
name|output
operator|.
name|append
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
break|break;
default|default :
name|output
operator|.
name|append
argument_list|(
name|input
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|output
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

