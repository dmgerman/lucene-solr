begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2009 www.imdict.net  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_class
DECL|class|AbstractDictionary
specifier|public
specifier|abstract
class|class
name|AbstractDictionary
block|{
comment|/**    * ç¬¬ä¸ä¸ªæ±å­ä¸ºâåâï¼ä»åé¢æ15ä¸ªåºï¼å±15*94ä¸ªå­ç¬¦    */
DECL|field|GB2312_FIRST_CHAR
specifier|public
specifier|static
specifier|final
name|int
name|GB2312_FIRST_CHAR
init|=
literal|1410
decl_stmt|;
comment|/**    * GB2312å­ç¬¦éä¸­01~87çå­ç¬¦éæå¯è½ææï¼å±8178ä¸ª    */
DECL|field|GB2312_CHAR_NUM
specifier|public
specifier|static
specifier|final
name|int
name|GB2312_CHAR_NUM
init|=
literal|87
operator|*
literal|94
decl_stmt|;
comment|/**    * è¯åºæä»¶ä¸­æ¶å½äº6768ä¸ªæ±å­çè¯é¢ç»è®¡    */
DECL|field|CHAR_NUM_IN_FILE
specifier|public
specifier|static
specifier|final
name|int
name|CHAR_NUM_IN_FILE
init|=
literal|6768
decl_stmt|;
comment|// =====================================================
comment|// code +0 +1 +2 +3 +4 +5 +6 +7 +8 +9 +A +B +C +D +E +F
comment|// B0A0 å é¿ å æ¨ å å å ç ç è¼ ç® è¾ ç¢ ç± é
comment|// B0B0 é æ°¨ å® ä¿º æ æ å²¸ èº æ¡ è® æ ç å¹ æ ç¬ ç¿±
comment|// B0C0 è¢ å² å¥¥ æ æ¾³ è­ æ æ å­ å§ ç¬ å« ç¤ å·´ æ è·
comment|// B0D0 é¶ æ è å é¸ ç½¢ ç¸ ç½ æ ç¾ æ ä½° è´¥ æ ç¨ æ
comment|// B0E0 ç­ æ¬ æ³ è¬ é¢ æ¿ ç æ® æ ä¼´ ç£ å å ç» é¦ å¸®
comment|// B0F0 æ¢ æ¦ è ç» æ£ ç£ è é å è°¤ è è å è¤ å¥
comment|// =====================================================
comment|//
comment|// GB2312 å­ç¬¦éçåºä½åå¸è¡¨ï¼
comment|// åºå· å­æ° å­ç¬¦ç±»å«
comment|// 01 94 ä¸è¬ç¬¦å·
comment|// 02 72 é¡ºåºå·ç 
comment|// 03 94 æä¸å­æ¯
comment|// 04 83 æ¥æåå
comment|// 05 86 Katakana
comment|// 06 48 å¸èå­æ¯
comment|// 07 66 ä¿æå­æ¯
comment|// 08 63 æ±è¯­æ¼é³ç¬¦å·
comment|// 09 76 å¾å½¢ç¬¦å·
comment|// 10-15 å¤ç¨åº
comment|// 16-55 3755 ä¸çº§æ±å­ï¼ä»¥æ¼é³ä¸ºåº
comment|// 56-87 3008 äºçº§æ±å­ï¼ä»¥ç¬åä¸ºåº
comment|// 88-94 å¤ç¨åº
comment|// ======================================================
comment|/**    * GB2312 å±æ¶å½æ 7445 ä¸ªå­ç¬¦ï¼å¶ä¸­ç®åæ±å­ 6763 ä¸ªï¼å­æ¯åç¬¦å· 682 ä¸ªã    *     * GB2312 å°ææ¶å½çå­ç¬¦åä¸º 94 ä¸ªåºï¼ç¼å·ä¸º 01 åºè³ 94 åºï¼æ¯ä¸ªåºæ¶å½ 94 ä¸ªå­ç¬¦ï¼ç¼å·ä¸º 01 ä½è³ 94    * ä½ï¼01ä¸ºèµ·å§ä¸0xA1ï¼94ä½å¤äº0xFEãGB2312 çæ¯ä¸ä¸ªå­ç¬¦é½ç±ä¸å¶å¯ä¸å¯¹åºçåºå·åä½å·æç¡®å®ãä¾å¦ï¼æ±å­âåâï¼ç¼å·ä¸º 16 åº 01    * ä½ã    */
comment|/**    * @param ccid    * @return    */
DECL|method|getCCByGB2312Id
specifier|public
name|String
name|getCCByGB2312Id
parameter_list|(
name|int
name|ccid
parameter_list|)
block|{
if|if
condition|(
name|ccid
argument_list|<
literal|0
operator|||
name|ccid
argument_list|>
name|WordDictionary
operator|.
name|GB2312_CHAR_NUM
condition|)
return|return
literal|""
return|;
name|int
name|cc1
init|=
name|ccid
operator|/
literal|94
operator|+
literal|161
decl_stmt|;
name|int
name|cc2
init|=
name|ccid
operator|%
literal|94
operator|+
literal|161
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|cc1
expr_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|cc2
expr_stmt|;
try|try
block|{
name|String
name|cchar
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|"GB2312"
argument_list|)
decl_stmt|;
return|return
name|cchar
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
comment|/**    * æ ¹æ®è¾å¥çUnicodeå­ç¬¦ï¼è·åå®çGB2312ç¼ç æèasciiç¼ç ï¼    *     * @param ch è¾å¥çGB2312ä¸­æå­ç¬¦æèASCIIå­ç¬¦(128ä¸ª)    * @return chå¨GB2312ä¸­çä½ç½®ï¼-1è¡¨ç¤ºè¯¥å­ç¬¦ä¸è®¤è¯    */
DECL|method|getGB2312Id
specifier|public
name|short
name|getGB2312Id
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
try|try
block|{
name|byte
index|[]
name|buffer
init|=
name|Character
operator|.
name|toString
argument_list|(
name|ch
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"GB2312"
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
comment|// æ­£å¸¸æåµä¸bufferåºè¯¥æ¯ä¸¤ä¸ªå­èï¼å¦åè¯´æchä¸å±äºGB2312ç¼ç ï¼æè¿å'?'ï¼æ­¤æ¶è¯´æä¸è®¤è¯è¯¥å­ç¬¦
return|return
operator|-
literal|1
return|;
block|}
name|int
name|b0
init|=
call|(
name|int
call|)
argument_list|(
name|buffer
index|[
literal|0
index|]
operator|&
literal|0x0FF
argument_list|)
operator|-
literal|161
decl_stmt|;
comment|// ç¼ç ä»A1å¼å§ï¼å æ­¤åå»0xA1=161
name|int
name|b1
init|=
call|(
name|int
call|)
argument_list|(
name|buffer
index|[
literal|1
index|]
operator|&
literal|0x0FF
argument_list|)
operator|-
literal|161
decl_stmt|;
comment|// ç¬¬ä¸ä¸ªå­ç¬¦åæåä¸ä¸ªå­ç¬¦æ²¡ææ±å­ï¼å æ­¤æ¯ä¸ªåºåªæ¶16*6-2=94ä¸ªæ±å­
return|return
call|(
name|short
call|)
argument_list|(
name|b0
operator|*
literal|94
operator|+
name|b1
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * æ¹è¿ç32ä½FNV hashç®æ³ï¼ç¨ä½æ¬ç¨åºä¸­çç¬¬ä¸hashå½æ°.ç¬¬ä¸åç¬¬äºhashå½æ°ç¨æ¥èåè®¡ç®hashè¡¨ï¼ ä½¿å¶åååå¸ï¼    * å¹¶è½é¿åå hashè¡¨è¿å¯èå¯¼è´çé¿æ¶é´è®¡ç®çé®é¢    *     * @param c å¾hashçUnicodeå­ç¬¦    * @return cçåå¸å¼    * @see Utility.hash2()    */
DECL|method|hash1
specifier|public
name|long
name|hash1
parameter_list|(
name|char
name|c
parameter_list|)
block|{
specifier|final
name|long
name|p
init|=
literal|1099511628211L
decl_stmt|;
name|long
name|hash
init|=
literal|0xcbf29ce484222325L
decl_stmt|;
name|hash
operator|=
operator|(
name|hash
operator|^
operator|(
name|c
operator|&
literal|0x00FF
operator|)
operator|)
operator|*
name|p
expr_stmt|;
name|hash
operator|=
operator|(
name|hash
operator|^
operator|(
name|c
operator|>>
literal|8
operator|)
operator|)
operator|*
name|p
expr_stmt|;
name|hash
operator|+=
name|hash
operator|<<
literal|13
expr_stmt|;
name|hash
operator|^=
name|hash
operator|>>
literal|7
expr_stmt|;
name|hash
operator|+=
name|hash
operator|<<
literal|3
expr_stmt|;
name|hash
operator|^=
name|hash
operator|>>
literal|17
expr_stmt|;
name|hash
operator|+=
name|hash
operator|<<
literal|5
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/**    * @see Utility.hash1(char[])    * @param carray    * @return    */
DECL|method|hash1
specifier|public
name|long
name|hash1
parameter_list|(
name|char
name|carray
index|[]
parameter_list|)
block|{
specifier|final
name|long
name|p
init|=
literal|1099511628211L
decl_stmt|;
name|long
name|hash
init|=
literal|0xcbf29ce484222325L
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
name|carray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|d
init|=
name|carray
index|[
name|i
index|]
decl_stmt|;
name|hash
operator|=
operator|(
name|hash
operator|^
operator|(
name|d
operator|&
literal|0x00FF
operator|)
operator|)
operator|*
name|p
expr_stmt|;
name|hash
operator|=
operator|(
name|hash
operator|^
operator|(
name|d
operator|>>
literal|8
operator|)
operator|)
operator|*
name|p
expr_stmt|;
block|}
comment|// hash += hash<< 13;
comment|// hash ^= hash>> 7;
comment|// hash += hash<< 3;
comment|// hash ^= hash>> 17;
comment|// hash += hash<< 5;
return|return
name|hash
return|;
block|}
comment|/**    * djb2åå¸ç®æ³ï¼ç¨ä½æ¬ç¨åºä¸­çç¬¬äºhashå½æ°    *     * djb2 hash algorithmï¼this algorithm (k=33) was first reported by dan    * bernstein many years ago in comp.lang.c. another version of this algorithm    * (now favored by bernstein) uses xor: hash(i) = hash(i - 1) * 33 ^ str[i];    * the magic of number 33 (why it works better than many other constants,    * prime or not) has never been adequately explained.    *     * @param c    * @return    */
DECL|method|hash2
specifier|public
name|int
name|hash2
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|int
name|hash
init|=
literal|5381
decl_stmt|;
comment|/* hash 33 + c */
name|hash
operator|=
operator|(
operator|(
name|hash
operator|<<
literal|5
operator|)
operator|+
name|hash
operator|)
operator|+
name|c
operator|&
literal|0x00FF
expr_stmt|;
name|hash
operator|=
operator|(
operator|(
name|hash
operator|<<
literal|5
operator|)
operator|+
name|hash
operator|)
operator|+
name|c
operator|>>
literal|8
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/**    * @see Utility.hash2(char[])    * @param carray    * @return    */
DECL|method|hash2
specifier|public
name|int
name|hash2
parameter_list|(
name|char
name|carray
index|[]
parameter_list|)
block|{
name|int
name|hash
init|=
literal|5381
decl_stmt|;
comment|/* hash 33 + c */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|carray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|d
init|=
name|carray
index|[
name|i
index|]
decl_stmt|;
name|hash
operator|=
operator|(
operator|(
name|hash
operator|<<
literal|5
operator|)
operator|+
name|hash
operator|)
operator|+
name|d
operator|&
literal|0x00FF
expr_stmt|;
name|hash
operator|=
operator|(
operator|(
name|hash
operator|<<
literal|5
operator|)
operator|+
name|hash
operator|)
operator|+
name|d
operator|>>
literal|8
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
block|}
end_class

end_unit

