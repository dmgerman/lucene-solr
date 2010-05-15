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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_comment
comment|/**  * Analyzer for testing  */
end_comment

begin_class
DECL|class|MockAnalyzer
specifier|public
specifier|final
class|class
name|MockAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** Acts Similar to WhitespaceAnalyzer */
DECL|field|WHITESPACE
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|WHITESPACE
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"[^ \t\r\n]+"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Acts Similar to KeywordAnalyzer.    * TODO: Keyword returns an "empty" token for an empty reader...     */
DECL|field|KEYWORD
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|KEYWORD
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|".*"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Acts like SimpleAnalyzer/LetterTokenizer. */
comment|// the ugly regex below is Unicode 5.2 [:Letter:]
DECL|field|SIMPLE
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|SIMPLE
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"[A-Za-zÂªÂµÂºÃ-ÃÃ-Ã¶Ã¸-ËË-ËË -Ë¤Ë¬Ë®Í°-Í´Í¶Í·Íº-Í½ÎÎ-ÎÎÎ-Î¡Î£-ÏµÏ·-ÒÒ-Ô¥Ô±-ÕÕÕ¡-Ö×-×ª×°-×²Ø¡-ÙÙ®Ù¯Ù±-ÛÛÛ¥Û¦Û®Û¯Ûº-Û¼Û¿ÜÜ-Ü¯Ý-Þ¥Þ±ß-ßªß´ßµßºà -à à à ¤à ¨à¤-à¤¹à¤½à¥à¥-à¥¡à¥±à¥²à¥¹-à¥¿à¦-à¦à¦à¦à¦-à¦¨à¦ª-à¦°à¦²à¦¶-à¦¹à¦½à§à§à§à§-à§¡à§°à§±à¨-à¨à¨à¨à¨-à¨¨à¨ª-à¨°à¨²à¨³à¨µà¨¶à¨¸à¨¹à©-à©à©à©²-à©´àª-àªàª-àªàª-àª¨àªª-àª°àª²àª³àªµ-àª¹àª½à«à« à«¡à¬-à¬à¬à¬à¬-à¬¨à¬ª-à¬°à¬²à¬³à¬µ-à¬¹à¬½à­à­à­-à­¡à­±à®à®-à®à®-à®à®-à®à®à®à®à®à®à®£à®¤à®¨-à®ªà®®-à®¹à¯à°-à°à°-à°à°-à°¨à°ª-à°³à°µ-à°¹à°½à±à±à± à±¡à²-à²à²-à²à²-à²¨à²ª-à²³à²µ-à²¹à²½à³à³ à³¡à´-à´à´-à´à´-à´¨à´ª-à´¹à´½àµ àµ¡àµº-àµ¿à¶-à¶à¶-à¶±à¶³-à¶»à¶½à·-à·à¸-à¸°à¸²à¸³à¹-à¹àºàºàºàºàºàºàºàº-àºàº-àºàº¡-àº£àº¥àº§àºªàº«àº­-àº°àº²àº³àº½à»-à»à»à»à»à¼à½-à½à½-à½¬à¾-à¾á-áªá¿á-áá-áá¡á¥á¦á®-á°áµ-ááá -áá-áºá¼á-áá-áá-ááá-áá -áá-áá-á°á²-áµá¸-á¾áá-áá-áá-áá-áá-áá-áá -á´á-á¬á¯-á¿á-áá -áªá-áá-áá -á±á-áá -á¬á®-á°á-á³ááá  -á¡·á¢-á¢¨á¢ªá¢°-á£µá¤-á¤á¥-á¥­á¥°-á¥´á¦-á¦«á§-á§á¨-á¨á¨ -á©áª§á¬-á¬³á­-á­á®-á® á®®á®¯á°-á°£á±-á±á±-á±½á³©-á³¬á³®-á³±á´-á¶¿á¸-á¼á¼-á¼á¼ -á½á½-á½á½-á½á½á½á½á½-á½½á¾-á¾´á¾¶-á¾¼á¾¾á¿-á¿á¿-á¿á¿-á¿á¿-á¿á¿ -á¿¬á¿²-á¿´á¿¶-á¿¼â±â¿â-ââââ-âââ-ââ¤â¦â¨âª-â­â¯-â¹â¼-â¿â-âââââ°-â°®â°°-â±â± -â³¤â³«-â³®â´-â´¥â´°-âµ¥âµ¯â¶-â¶â¶ -â¶¦â¶¨-â¶®â¶°-â¶¶â¶¸-â¶¾â·-â·â·-â·â·-â·â·-â·â¸¯ããã±-ãµã»ã¼ã-ãã-ãã¡-ãºã¼-ã¿ã-ã­ã±-ãã -ã·ã°-ã¿ã-ä¶µä¸-é¿ê-êê-ê½ê-êê-êêªê«ê-êê¢-ê®ê¿-êê -ê¥ê-êê¢-êêêê»-ê ê -ê ê -ê ê -ê ¢ê¡-ê¡³ê¢-ê¢³ê£²-ê£·ê£»ê¤-ê¤¥ê¤°-ê¥ê¥ -ê¥¼ê¦-ê¦²ê§ê¨-ê¨¨ê©-ê©ê©-ê©ê© -ê©¶ê©ºêª-êª¯êª±êªµêª¶êª¹-êª½ê«ê«ê«-ê«ê¯-ê¯¢ê°-í£í°-íí-í»ï¤-ï¨­ï¨°-ï©­ï©°-ï«ï¬-ï¬ï¬-ï¬ï¬ï¬-ï¬¨ï¬ª-ï¬¶ï¬¸-ï¬¼ï¬¾ï­ï­ï­ï­ï­-ï®±ï¯-ï´½ïµ-ï¶ï¶-ï·ï·°-ï·»ï¹°-ï¹´ï¹¶-ï»¼ï¼¡-ï¼ºï½-ï½ï½¦-ï¾¾ï¿-ï¿ï¿-ï¿ï¿-ï¿ï¿-ï¿ð-ðð-ð¦ð¨-ðºð¼ð½ð¿-ðð-ðð-ðºð-ðð -ðð-ðð°-ðð-ðð-ðð -ðð-ðð-ðð -ð ð ð -ð µð ·ð ¸ð ¼ð ¿-ð¡ð¤-ð¤ð¤ -ð¤¹ð¨ð¨-ð¨ð¨-ð¨ð¨-ð¨³ð© -ð©¼ð¬-ð¬µð­-ð­ð­ -ð­²ð°-ð±ð-ð¯ð-ð®ð-ð®ð-ðð-ðððð¢ð¥ð¦ð©-ð¬ð®-ð¹ð»ð½-ðð-ðð-ðð-ðð-ðð-ð¹ð»-ð¾ð-ððð-ðð-ð¥ð¨-ðð-ðð-ðºð¼-ðð-ð´ð¶-ðð-ð®ð°-ðð-ð¨ðª-ðð-ðð -ðªðª-ð«´ð¯ -ð¯¨]+"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|runAutomaton
specifier|private
specifier|final
name|CharacterRunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|lowerCase
specifier|private
specifier|final
name|boolean
name|lowerCase
decl_stmt|;
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|)
block|{
name|this
operator|.
name|runAutomaton
operator|=
name|runAutomaton
expr_stmt|;
name|this
operator|.
name|lowerCase
operator|=
name|lowerCase
expr_stmt|;
block|}
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|MockTokenizer
name|t
init|=
operator|(
name|MockTokenizer
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|t
operator|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|t
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

