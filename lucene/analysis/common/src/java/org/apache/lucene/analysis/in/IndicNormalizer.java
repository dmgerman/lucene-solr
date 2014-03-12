begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.in
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|in
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Character
operator|.
name|UnicodeBlock
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
name|StemmerUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Normalizes the Unicode representation of text in Indian languages.  *<p>  * Follows guidelines from Unicode 5.2, chapter 6, South Asian Scripts I  * and graphical decompositions from http://ldc.upenn.edu/myl/IndianScriptsUnicode.html  *</p>  */
end_comment

begin_class
DECL|class|IndicNormalizer
specifier|public
class|class
name|IndicNormalizer
block|{
DECL|class|ScriptData
specifier|private
specifier|static
class|class
name|ScriptData
block|{
DECL|field|flag
specifier|final
name|int
name|flag
decl_stmt|;
DECL|field|base
specifier|final
name|int
name|base
decl_stmt|;
DECL|field|decompMask
name|BitSet
name|decompMask
decl_stmt|;
DECL|method|ScriptData
name|ScriptData
parameter_list|(
name|int
name|flag
parameter_list|,
name|int
name|base
parameter_list|)
block|{
name|this
operator|.
name|flag
operator|=
name|flag
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
block|}
DECL|field|scripts
specifier|private
specifier|static
specifier|final
name|IdentityHashMap
argument_list|<
name|Character
operator|.
name|UnicodeBlock
argument_list|,
name|ScriptData
argument_list|>
name|scripts
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|(
literal|9
argument_list|)
decl_stmt|;
DECL|method|flag
specifier|private
specifier|static
name|int
name|flag
parameter_list|(
name|Character
operator|.
name|UnicodeBlock
name|ub
parameter_list|)
block|{
return|return
name|scripts
operator|.
name|get
argument_list|(
name|ub
argument_list|)
operator|.
name|flag
return|;
block|}
static|static
block|{
name|scripts
operator|.
name|put
argument_list|(
name|DEVANAGARI
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|1
argument_list|,
literal|0x0900
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|BENGALI
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|2
argument_list|,
literal|0x0980
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|GURMUKHI
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|4
argument_list|,
literal|0x0A00
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|GUJARATI
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|8
argument_list|,
literal|0x0A80
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|ORIYA
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|16
argument_list|,
literal|0x0B00
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|TAMIL
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|32
argument_list|,
literal|0x0B80
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|TELUGU
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|64
argument_list|,
literal|0x0C00
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|KANNADA
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|128
argument_list|,
literal|0x0C80
argument_list|)
argument_list|)
expr_stmt|;
name|scripts
operator|.
name|put
argument_list|(
name|MALAYALAM
argument_list|,
operator|new
name|ScriptData
argument_list|(
literal|256
argument_list|,
literal|0x0D00
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decompositions according to Unicode 5.2,     * and http://ldc.upenn.edu/myl/IndianScriptsUnicode.html    *     * Most of these are not handled by unicode normalization anyway.    *     * The numbers here represent offsets into the respective codepages,    * with -1 representing null and 0xFF representing zero-width joiner.    *     * the columns are: ch1, ch2, ch3, res, flags    * ch1, ch2, and ch3 are the decomposition    * res is the composition, and flags are the scripts to which it applies.    */
DECL|field|decompositions
specifier|private
specifier|static
specifier|final
name|int
name|decompositions
index|[]
index|[]
init|=
block|{
comment|/* devanagari, gujarati vowel candra O */
block|{
literal|0x05
block|,
literal|0x3E
block|,
literal|0x45
block|,
literal|0x11
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari short O */
block|{
literal|0x05
block|,
literal|0x3E
block|,
literal|0x46
block|,
literal|0x12
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati letter O */
block|{
literal|0x05
block|,
literal|0x3E
block|,
literal|0x47
block|,
literal|0x13
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari letter AI, gujarati letter AU */
block|{
literal|0x05
block|,
literal|0x3E
block|,
literal|0x48
block|,
literal|0x14
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari, bengali, gurmukhi, gujarati, oriya AA */
block|{
literal|0x05
block|,
literal|0x3E
block|,
operator|-
literal|1
block|,
literal|0x06
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|BENGALI
argument_list|)
operator||
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
operator||
name|flag
argument_list|(
name|ORIYA
argument_list|)
block|}
block|,
comment|/* devanagari letter candra A */
block|{
literal|0x05
block|,
literal|0x45
block|,
operator|-
literal|1
block|,
literal|0x72
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* gujarati vowel candra E */
block|{
literal|0x05
block|,
literal|0x45
block|,
operator|-
literal|1
block|,
literal|0x0D
block|,
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari letter short A */
block|{
literal|0x05
block|,
literal|0x46
block|,
operator|-
literal|1
block|,
literal|0x04
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* gujarati letter E */
block|{
literal|0x05
block|,
literal|0x47
block|,
operator|-
literal|1
block|,
literal|0x0F
block|,
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* gurmukhi, gujarati letter AI */
block|{
literal|0x05
block|,
literal|0x48
block|,
operator|-
literal|1
block|,
literal|0x10
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati vowel candra O */
block|{
literal|0x05
block|,
literal|0x49
block|,
operator|-
literal|1
block|,
literal|0x11
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari short O */
block|{
literal|0x05
block|,
literal|0x4A
block|,
operator|-
literal|1
block|,
literal|0x12
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati letter O */
block|{
literal|0x05
block|,
literal|0x4B
block|,
operator|-
literal|1
block|,
literal|0x13
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari letter AI, gurmukhi letter AU, gujarati letter AU */
block|{
literal|0x05
block|,
literal|0x4C
block|,
operator|-
literal|1
block|,
literal|0x14
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati vowel candra O */
block|{
literal|0x06
block|,
literal|0x45
block|,
operator|-
literal|1
block|,
literal|0x11
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari short O */
block|{
literal|0x06
block|,
literal|0x46
block|,
operator|-
literal|1
block|,
literal|0x12
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati letter O */
block|{
literal|0x06
block|,
literal|0x47
block|,
operator|-
literal|1
block|,
literal|0x13
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari letter AI, gujarati letter AU */
block|{
literal|0x06
block|,
literal|0x48
block|,
operator|-
literal|1
block|,
literal|0x14
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* malayalam letter II */
block|{
literal|0x07
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x08
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* devanagari letter UU */
block|{
literal|0x09
block|,
literal|0x41
block|,
operator|-
literal|1
block|,
literal|0x0A
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* tamil, malayalam letter UU (some styles) */
block|{
literal|0x09
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x0A
block|,
name|flag
argument_list|(
name|TAMIL
argument_list|)
operator||
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* malayalam letter AI */
block|{
literal|0x0E
block|,
literal|0x46
block|,
operator|-
literal|1
block|,
literal|0x10
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* devanagari candra E */
block|{
literal|0x0F
block|,
literal|0x45
block|,
operator|-
literal|1
block|,
literal|0x0D
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari short E */
block|{
literal|0x0F
block|,
literal|0x46
block|,
operator|-
literal|1
block|,
literal|0x0E
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari AI */
block|{
literal|0x0F
block|,
literal|0x47
block|,
operator|-
literal|1
block|,
literal|0x10
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* oriya AI */
block|{
literal|0x0F
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x10
block|,
name|flag
argument_list|(
name|ORIYA
argument_list|)
block|}
block|,
comment|/* malayalam letter OO */
block|{
literal|0x12
block|,
literal|0x3E
block|,
operator|-
literal|1
block|,
literal|0x13
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* telugu, kannada letter AU */
block|{
literal|0x12
block|,
literal|0x4C
block|,
operator|-
literal|1
block|,
literal|0x14
block|,
name|flag
argument_list|(
name|TELUGU
argument_list|)
operator||
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* telugu letter OO */
block|{
literal|0x12
block|,
literal|0x55
block|,
operator|-
literal|1
block|,
literal|0x13
block|,
name|flag
argument_list|(
name|TELUGU
argument_list|)
block|}
block|,
comment|/* tamil, malayalam letter AU */
block|{
literal|0x12
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x14
block|,
name|flag
argument_list|(
name|TAMIL
argument_list|)
operator||
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* oriya letter AU */
block|{
literal|0x13
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x14
block|,
name|flag
argument_list|(
name|ORIYA
argument_list|)
block|}
block|,
comment|/* devanagari qa */
block|{
literal|0x15
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x58
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari, gurmukhi khha */
block|{
literal|0x16
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x59
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* devanagari, gurmukhi ghha */
block|{
literal|0x17
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x5A
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* devanagari, gurmukhi za */
block|{
literal|0x1C
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x5B
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* devanagari dddha, bengali, oriya rra */
block|{
literal|0x21
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x5C
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|BENGALI
argument_list|)
operator||
name|flag
argument_list|(
name|ORIYA
argument_list|)
block|}
block|,
comment|/* devanagari, bengali, oriya rha */
block|{
literal|0x22
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x5D
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|BENGALI
argument_list|)
operator||
name|flag
argument_list|(
name|ORIYA
argument_list|)
block|}
block|,
comment|/* malayalam chillu nn */
block|{
literal|0x23
block|,
literal|0x4D
block|,
literal|0xFF
block|,
literal|0x7A
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* bengali khanda ta */
block|{
literal|0x24
block|,
literal|0x4D
block|,
literal|0xFF
block|,
literal|0x4E
block|,
name|flag
argument_list|(
name|BENGALI
argument_list|)
block|}
block|,
comment|/* devanagari nnna */
block|{
literal|0x28
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x29
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* malayalam chillu n */
block|{
literal|0x28
block|,
literal|0x4D
block|,
literal|0xFF
block|,
literal|0x7B
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* devanagari, gurmukhi fa */
block|{
literal|0x2B
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x5E
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* devanagari, bengali yya */
block|{
literal|0x2F
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x5F
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|BENGALI
argument_list|)
block|}
block|,
comment|/* telugu letter vocalic R */
block|{
literal|0x2C
block|,
literal|0x41
block|,
literal|0x41
block|,
literal|0x0B
block|,
name|flag
argument_list|(
name|TELUGU
argument_list|)
block|}
block|,
comment|/* devanagari rra */
block|{
literal|0x30
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x31
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* malayalam chillu rr */
block|{
literal|0x30
block|,
literal|0x4D
block|,
literal|0xFF
block|,
literal|0x7C
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* malayalam chillu l */
block|{
literal|0x32
block|,
literal|0x4D
block|,
literal|0xFF
block|,
literal|0x7D
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* devanagari llla */
block|{
literal|0x33
block|,
literal|0x3C
block|,
operator|-
literal|1
block|,
literal|0x34
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* malayalam chillu ll */
block|{
literal|0x33
block|,
literal|0x4D
block|,
literal|0xFF
block|,
literal|0x7E
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* telugu letter MA */
block|{
literal|0x35
block|,
literal|0x41
block|,
operator|-
literal|1
block|,
literal|0x2E
block|,
name|flag
argument_list|(
name|TELUGU
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati vowel sign candra O */
block|{
literal|0x3E
block|,
literal|0x45
block|,
operator|-
literal|1
block|,
literal|0x49
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari vowel sign short O */
block|{
literal|0x3E
block|,
literal|0x46
block|,
operator|-
literal|1
block|,
literal|0x4A
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati vowel sign O */
block|{
literal|0x3E
block|,
literal|0x47
block|,
operator|-
literal|1
block|,
literal|0x4B
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* devanagari, gujarati vowel sign AU */
block|{
literal|0x3E
block|,
literal|0x48
block|,
operator|-
literal|1
block|,
literal|0x4C
block|,
name|flag
argument_list|(
name|DEVANAGARI
argument_list|)
operator||
name|flag
argument_list|(
name|GUJARATI
argument_list|)
block|}
block|,
comment|/* kannada vowel sign II */
block|{
literal|0x3F
block|,
literal|0x55
block|,
operator|-
literal|1
block|,
literal|0x40
block|,
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* gurmukhi vowel sign UU (when stacking) */
block|{
literal|0x41
block|,
literal|0x41
block|,
operator|-
literal|1
block|,
literal|0x42
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* tamil, malayalam vowel sign O */
block|{
literal|0x46
block|,
literal|0x3E
block|,
operator|-
literal|1
block|,
literal|0x4A
block|,
name|flag
argument_list|(
name|TAMIL
argument_list|)
operator||
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* kannada vowel sign OO */
block|{
literal|0x46
block|,
literal|0x42
block|,
literal|0x55
block|,
literal|0x4B
block|,
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* kannada vowel sign O */
block|{
literal|0x46
block|,
literal|0x42
block|,
operator|-
literal|1
block|,
literal|0x4A
block|,
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* malayalam vowel sign AI (if reordered twice) */
block|{
literal|0x46
block|,
literal|0x46
block|,
operator|-
literal|1
block|,
literal|0x48
block|,
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* telugu, kannada vowel sign EE */
block|{
literal|0x46
block|,
literal|0x55
block|,
operator|-
literal|1
block|,
literal|0x47
block|,
name|flag
argument_list|(
name|TELUGU
argument_list|)
operator||
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* telugu, kannada vowel sign AI */
block|{
literal|0x46
block|,
literal|0x56
block|,
operator|-
literal|1
block|,
literal|0x48
block|,
name|flag
argument_list|(
name|TELUGU
argument_list|)
operator||
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* tamil, malayalam vowel sign AU */
block|{
literal|0x46
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x4C
block|,
name|flag
argument_list|(
name|TAMIL
argument_list|)
operator||
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* bengali, oriya vowel sign O, tamil, malayalam vowel sign OO */
block|{
literal|0x47
block|,
literal|0x3E
block|,
operator|-
literal|1
block|,
literal|0x4B
block|,
name|flag
argument_list|(
name|BENGALI
argument_list|)
operator||
name|flag
argument_list|(
name|ORIYA
argument_list|)
operator||
name|flag
argument_list|(
name|TAMIL
argument_list|)
operator||
name|flag
argument_list|(
name|MALAYALAM
argument_list|)
block|}
block|,
comment|/* bengali, oriya vowel sign AU */
block|{
literal|0x47
block|,
literal|0x57
block|,
operator|-
literal|1
block|,
literal|0x4C
block|,
name|flag
argument_list|(
name|BENGALI
argument_list|)
operator||
name|flag
argument_list|(
name|ORIYA
argument_list|)
block|}
block|,
comment|/* kannada vowel sign OO */
block|{
literal|0x4A
block|,
literal|0x55
block|,
operator|-
literal|1
block|,
literal|0x4B
block|,
name|flag
argument_list|(
name|KANNADA
argument_list|)
block|}
block|,
comment|/* gurmukhi letter I */
block|{
literal|0x72
block|,
literal|0x3F
block|,
operator|-
literal|1
block|,
literal|0x07
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* gurmukhi letter II */
block|{
literal|0x72
block|,
literal|0x40
block|,
operator|-
literal|1
block|,
literal|0x08
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* gurmukhi letter EE */
block|{
literal|0x72
block|,
literal|0x47
block|,
operator|-
literal|1
block|,
literal|0x0F
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* gurmukhi letter U */
block|{
literal|0x73
block|,
literal|0x41
block|,
operator|-
literal|1
block|,
literal|0x09
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* gurmukhi letter UU */
block|{
literal|0x73
block|,
literal|0x42
block|,
operator|-
literal|1
block|,
literal|0x0A
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,
comment|/* gurmukhi letter OO */
block|{
literal|0x73
block|,
literal|0x4B
block|,
operator|-
literal|1
block|,
literal|0x13
block|,
name|flag
argument_list|(
name|GURMUKHI
argument_list|)
block|}
block|,   }
decl_stmt|;
static|static
block|{
for|for
control|(
name|ScriptData
name|sd
range|:
name|scripts
operator|.
name|values
argument_list|()
control|)
block|{
name|sd
operator|.
name|decompMask
operator|=
operator|new
name|BitSet
argument_list|(
literal|0x7F
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|decompositions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|ch
init|=
name|decompositions
index|[
name|i
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|int
name|flags
init|=
name|decompositions
index|[
name|i
index|]
index|[
literal|4
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|sd
operator|.
name|flag
operator|)
operator|!=
literal|0
condition|)
name|sd
operator|.
name|decompMask
operator|.
name|set
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Normalizes input text, and returns the new length.    * The length will always be less than or equal to the existing length.    *     * @param text input text    * @param len valid length    * @return normalized length    */
DECL|method|normalize
specifier|public
name|int
name|normalize
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Character
operator|.
name|UnicodeBlock
name|block
init|=
name|Character
operator|.
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|text
index|[
name|i
index|]
argument_list|)
decl_stmt|;
specifier|final
name|ScriptData
name|sd
init|=
name|scripts
operator|.
name|get
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|sd
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|ch
init|=
name|text
index|[
name|i
index|]
operator|-
name|sd
operator|.
name|base
decl_stmt|;
if|if
condition|(
name|sd
operator|.
name|decompMask
operator|.
name|get
argument_list|(
name|ch
argument_list|)
condition|)
name|len
operator|=
name|compose
argument_list|(
name|ch
argument_list|,
name|block
argument_list|,
name|sd
argument_list|,
name|text
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|len
return|;
block|}
comment|/**    * Compose into standard form any compositions in the decompositions table.    */
DECL|method|compose
specifier|private
name|int
name|compose
parameter_list|(
name|int
name|ch0
parameter_list|,
name|Character
operator|.
name|UnicodeBlock
name|block0
parameter_list|,
name|ScriptData
name|sd
parameter_list|,
name|char
name|text
index|[]
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|+
literal|1
operator|>=
name|len
condition|)
comment|/* need at least 2 chars! */
return|return
name|len
return|;
specifier|final
name|int
name|ch1
init|=
name|text
index|[
name|pos
operator|+
literal|1
index|]
operator|-
name|sd
operator|.
name|base
decl_stmt|;
specifier|final
name|Character
operator|.
name|UnicodeBlock
name|block1
init|=
name|Character
operator|.
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|text
index|[
name|pos
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|block1
operator|!=
name|block0
condition|)
comment|/* needs to be the same writing system */
return|return
name|len
return|;
name|int
name|ch2
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|pos
operator|+
literal|2
operator|<
name|len
condition|)
block|{
name|ch2
operator|=
name|text
index|[
name|pos
operator|+
literal|2
index|]
operator|-
name|sd
operator|.
name|base
expr_stmt|;
name|Character
operator|.
name|UnicodeBlock
name|block2
init|=
name|Character
operator|.
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|text
index|[
name|pos
operator|+
literal|2
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|text
index|[
name|pos
operator|+
literal|2
index|]
operator|==
literal|'\u200D'
condition|)
comment|// ZWJ
name|ch2
operator|=
literal|0xFF
expr_stmt|;
elseif|else
if|if
condition|(
name|block2
operator|!=
name|block1
condition|)
comment|// still allow a 2-char match
name|ch2
operator|=
operator|-
literal|1
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|decompositions
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|decompositions
index|[
name|i
index|]
index|[
literal|0
index|]
operator|==
name|ch0
operator|&&
operator|(
name|decompositions
index|[
name|i
index|]
index|[
literal|4
index|]
operator|&
name|sd
operator|.
name|flag
operator|)
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|decompositions
index|[
name|i
index|]
index|[
literal|1
index|]
operator|==
name|ch1
operator|&&
operator|(
name|decompositions
index|[
name|i
index|]
index|[
literal|2
index|]
operator|<
literal|0
operator|||
name|decompositions
index|[
name|i
index|]
index|[
literal|2
index|]
operator|==
name|ch2
operator|)
condition|)
block|{
name|text
index|[
name|pos
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|sd
operator|.
name|base
operator|+
name|decompositions
index|[
name|i
index|]
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|len
operator|=
name|delete
argument_list|(
name|text
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|decompositions
index|[
name|i
index|]
index|[
literal|2
index|]
operator|>=
literal|0
condition|)
name|len
operator|=
name|delete
argument_list|(
name|text
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

