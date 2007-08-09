begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * RussianCharsets class contains encodings schemes (charsets) and toLowerCase() method implementation  * for russian characters in Unicode, KOI8 and CP1252.  * Each encoding scheme contains lowercase (positions 0-31) and uppercase (position 32-63) characters.  * One should be able to add other encoding schemes (like ISO-8859-5 or customized) by adding a new charset  * and adding logic to toLowerCase() method for that charset.  *  *  * @version $Id$  */
end_comment

begin_class
DECL|class|RussianCharsets
specifier|public
class|class
name|RussianCharsets
block|{
comment|// Unicode Russian charset (lowercase letters only)
DECL|field|UnicodeRussian
specifier|public
specifier|static
name|char
index|[]
name|UnicodeRussian
init|=
block|{
literal|'\u0430'
block|,
literal|'\u0431'
block|,
literal|'\u0432'
block|,
literal|'\u0433'
block|,
literal|'\u0434'
block|,
literal|'\u0435'
block|,
literal|'\u0436'
block|,
literal|'\u0437'
block|,
literal|'\u0438'
block|,
literal|'\u0439'
block|,
literal|'\u043A'
block|,
literal|'\u043B'
block|,
literal|'\u043C'
block|,
literal|'\u043D'
block|,
literal|'\u043E'
block|,
literal|'\u043F'
block|,
literal|'\u0440'
block|,
literal|'\u0441'
block|,
literal|'\u0442'
block|,
literal|'\u0443'
block|,
literal|'\u0444'
block|,
literal|'\u0445'
block|,
literal|'\u0446'
block|,
literal|'\u0447'
block|,
literal|'\u0448'
block|,
literal|'\u0449'
block|,
literal|'\u044A'
block|,
literal|'\u044B'
block|,
literal|'\u044C'
block|,
literal|'\u044D'
block|,
literal|'\u044E'
block|,
literal|'\u044F'
block|,
comment|// upper case
literal|'\u0410'
block|,
literal|'\u0411'
block|,
literal|'\u0412'
block|,
literal|'\u0413'
block|,
literal|'\u0414'
block|,
literal|'\u0415'
block|,
literal|'\u0416'
block|,
literal|'\u0417'
block|,
literal|'\u0418'
block|,
literal|'\u0419'
block|,
literal|'\u041A'
block|,
literal|'\u041B'
block|,
literal|'\u041C'
block|,
literal|'\u041D'
block|,
literal|'\u041E'
block|,
literal|'\u041F'
block|,
literal|'\u0420'
block|,
literal|'\u0421'
block|,
literal|'\u0422'
block|,
literal|'\u0423'
block|,
literal|'\u0424'
block|,
literal|'\u0425'
block|,
literal|'\u0426'
block|,
literal|'\u0427'
block|,
literal|'\u0428'
block|,
literal|'\u0429'
block|,
literal|'\u042A'
block|,
literal|'\u042B'
block|,
literal|'\u042C'
block|,
literal|'\u042D'
block|,
literal|'\u042E'
block|,
literal|'\u042F'
block|}
decl_stmt|;
comment|// KOI8 charset
DECL|field|KOI8
specifier|public
specifier|static
name|char
index|[]
name|KOI8
init|=
block|{
literal|0xc1
block|,
literal|0xc2
block|,
literal|0xd7
block|,
literal|0xc7
block|,
literal|0xc4
block|,
literal|0xc5
block|,
literal|0xd6
block|,
literal|0xda
block|,
literal|0xc9
block|,
literal|0xca
block|,
literal|0xcb
block|,
literal|0xcc
block|,
literal|0xcd
block|,
literal|0xce
block|,
literal|0xcf
block|,
literal|0xd0
block|,
literal|0xd2
block|,
literal|0xd3
block|,
literal|0xd4
block|,
literal|0xd5
block|,
literal|0xc6
block|,
literal|0xc8
block|,
literal|0xc3
block|,
literal|0xde
block|,
literal|0xdb
block|,
literal|0xdd
block|,
literal|0xdf
block|,
literal|0xd9
block|,
literal|0xd8
block|,
literal|0xdc
block|,
literal|0xc0
block|,
literal|0xd1
block|,
comment|// upper case
literal|0xe1
block|,
literal|0xe2
block|,
literal|0xf7
block|,
literal|0xe7
block|,
literal|0xe4
block|,
literal|0xe5
block|,
literal|0xf6
block|,
literal|0xfa
block|,
literal|0xe9
block|,
literal|0xea
block|,
literal|0xeb
block|,
literal|0xec
block|,
literal|0xed
block|,
literal|0xee
block|,
literal|0xef
block|,
literal|0xf0
block|,
literal|0xf2
block|,
literal|0xf3
block|,
literal|0xf4
block|,
literal|0xf5
block|,
literal|0xe6
block|,
literal|0xe8
block|,
literal|0xe3
block|,
literal|0xfe
block|,
literal|0xfb
block|,
literal|0xfd
block|,
literal|0xff
block|,
literal|0xf9
block|,
literal|0xf8
block|,
literal|0xfc
block|,
literal|0xe0
block|,
literal|0xf1
block|}
decl_stmt|;
comment|// CP1251 eharset
DECL|field|CP1251
specifier|public
specifier|static
name|char
index|[]
name|CP1251
init|=
block|{
literal|0xE0
block|,
literal|0xE1
block|,
literal|0xE2
block|,
literal|0xE3
block|,
literal|0xE4
block|,
literal|0xE5
block|,
literal|0xE6
block|,
literal|0xE7
block|,
literal|0xE8
block|,
literal|0xE9
block|,
literal|0xEA
block|,
literal|0xEB
block|,
literal|0xEC
block|,
literal|0xED
block|,
literal|0xEE
block|,
literal|0xEF
block|,
literal|0xF0
block|,
literal|0xF1
block|,
literal|0xF2
block|,
literal|0xF3
block|,
literal|0xF4
block|,
literal|0xF5
block|,
literal|0xF6
block|,
literal|0xF7
block|,
literal|0xF8
block|,
literal|0xF9
block|,
literal|0xFA
block|,
literal|0xFB
block|,
literal|0xFC
block|,
literal|0xFD
block|,
literal|0xFE
block|,
literal|0xFF
block|,
comment|// upper case
literal|0xC0
block|,
literal|0xC1
block|,
literal|0xC2
block|,
literal|0xC3
block|,
literal|0xC4
block|,
literal|0xC5
block|,
literal|0xC6
block|,
literal|0xC7
block|,
literal|0xC8
block|,
literal|0xC9
block|,
literal|0xCA
block|,
literal|0xCB
block|,
literal|0xCC
block|,
literal|0xCD
block|,
literal|0xCE
block|,
literal|0xCF
block|,
literal|0xD0
block|,
literal|0xD1
block|,
literal|0xD2
block|,
literal|0xD3
block|,
literal|0xD4
block|,
literal|0xD5
block|,
literal|0xD6
block|,
literal|0xD7
block|,
literal|0xD8
block|,
literal|0xD9
block|,
literal|0xDA
block|,
literal|0xDB
block|,
literal|0xDC
block|,
literal|0xDD
block|,
literal|0xDE
block|,
literal|0xDF
block|}
decl_stmt|;
DECL|method|toLowerCase
specifier|public
specifier|static
name|char
name|toLowerCase
parameter_list|(
name|char
name|letter
parameter_list|,
name|char
index|[]
name|charset
parameter_list|)
block|{
if|if
condition|(
name|charset
operator|==
name|UnicodeRussian
condition|)
block|{
if|if
condition|(
name|letter
operator|>=
literal|'\u0430'
operator|&&
name|letter
operator|<=
literal|'\u044F'
condition|)
block|{
return|return
name|letter
return|;
block|}
if|if
condition|(
name|letter
operator|>=
literal|'\u0410'
operator|&&
name|letter
operator|<=
literal|'\u042F'
condition|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|letter
operator|+
literal|32
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|charset
operator|==
name|KOI8
condition|)
block|{
if|if
condition|(
name|letter
operator|>=
literal|0xe0
operator|&&
name|letter
operator|<=
literal|0xff
condition|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|letter
operator|-
literal|32
argument_list|)
return|;
block|}
if|if
condition|(
name|letter
operator|>=
literal|0xc0
operator|&&
name|letter
operator|<=
literal|0xdf
condition|)
block|{
return|return
name|letter
return|;
block|}
block|}
if|if
condition|(
name|charset
operator|==
name|CP1251
condition|)
block|{
if|if
condition|(
name|letter
operator|>=
literal|0xC0
operator|&&
name|letter
operator|<=
literal|0xDF
condition|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|letter
operator|+
literal|32
argument_list|)
return|;
block|}
if|if
condition|(
name|letter
operator|>=
literal|0xE0
operator|&&
name|letter
operator|<=
literal|0xFF
condition|)
block|{
return|return
name|letter
return|;
block|}
block|}
return|return
name|Character
operator|.
name|toLowerCase
argument_list|(
name|letter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

