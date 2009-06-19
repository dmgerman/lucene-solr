begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Analyzer
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
name|PorterStemFilter
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
name|StopFilter
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
name|TokenStream
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
name|cn
operator|.
name|smart
operator|.
name|SentenceTokenizer
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
name|cn
operator|.
name|smart
operator|.
name|WordSegmenter
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
name|cn
operator|.
name|smart
operator|.
name|WordTokenizer
import|;
end_import

begin_comment
comment|/**  *   * SmartChineseAnalyzer æ¯ä¸ä¸ªæºè½ä¸­æåè¯æ¨¡åï¼ è½å¤å©ç¨æ¦çå¯¹æ±è¯­å¥å­è¿è¡æä¼ååï¼  * å¹¶ååµè±ætokenizerï¼è½ææå¤çä¸­è±ææ··åçææ¬åå®¹ã  *   * å®çåçåºäºèªç¶è¯­è¨å¤çé¢åçéé©¬å°ç§å¤«æ¨¡å(HMM)ï¼ å©ç¨å¤§éè¯­æåºçè®­ç»æ¥ç»è®¡æ±è¯­è¯æ±çè¯é¢åè·³è½¬æ¦çï¼  * ä»èæ ¹æ®è¿äºç»è®¡ç»æå¯¹æ´ä¸ªæ±è¯­å¥å­è®¡ç®æä¼¼ç¶(likelihood)çååã  *   * å ä¸ºæºè½åè¯éè¦è¯å¸æ¥ä¿å­è¯æ±çç»è®¡å¼ï¼SmartChineseAnalyzerçè¿è¡éè¦æå®è¯å¸ä½ç½®ï¼å¦ä½æå®è¯å¸ä½ç½®è¯·åè  * org.apache.lucene.analysis.cn.smart.AnalyzerProfile  *   * SmartChineseAnalyzerçç®æ³åè¯­æåºè¯å¸æ¥èªäºictclas1.0é¡¹ç®(http://www.ictclas.org)ï¼  * å¶ä¸­è¯å¸å·²è·åwww.ictclas.orgçapache license v2(APLv2)çææãå¨éµå¾ªAPLv2çæ¡ä»¶ä¸ï¼æ¬¢è¿ç¨æ·ä½¿ç¨ã  * å¨æ­¤æè°¢www.ictclas.orgä»¥åictclasåè¯è½¯ä»¶çå·¥ä½äººåçæ ç§å¥ç®ï¼  *   * @see org.apache.lucene.analysis.cn.smart.AnalyzerProfile  *   */
end_comment

begin_class
DECL|class|SmartChineseAnalyzer
specifier|public
class|class
name|SmartChineseAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopWords
specifier|private
name|Set
name|stopWords
init|=
literal|null
decl_stmt|;
DECL|field|wordSegment
specifier|private
name|WordSegmenter
name|wordSegment
decl_stmt|;
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * SmartChineseAnalyzeråé¨å¸¦æé»è®¤åæ­¢è¯åºï¼ä¸»è¦æ¯æ ç¹ç¬¦å·ãå¦æä¸å¸æç»æä¸­åºç°æ ç¹ç¬¦å·ï¼    * å¯ä»¥å°useDefaultStopWordsè®¾ä¸ºtrueï¼ useDefaultStopWordsä¸ºfalseæ¶ä¸ä½¿ç¨ä»»ä½åæ­¢è¯    *     * @param useDefaultStopWords    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|boolean
name|useDefaultStopWords
parameter_list|)
block|{
if|if
condition|(
name|useDefaultStopWords
condition|)
block|{
name|stopWords
operator|=
name|loadStopWords
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"stopwords.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|wordSegment
operator|=
operator|new
name|WordSegmenter
argument_list|()
expr_stmt|;
block|}
comment|/**    * ä½¿ç¨èªå®ä¹çèä¸ä½¿ç¨åç½®çåæ­¢è¯åºï¼åæ­¢è¯å¯ä»¥ä½¿ç¨SmartChineseAnalyzer.loadStopWords(InputStream)å è½½    *     * @param stopWords    * @see SmartChineseAnalyzer.loadStopWords(InputStream)    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|Set
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
name|wordSegment
operator|=
operator|new
name|WordSegmenter
argument_list|()
expr_stmt|;
block|}
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
name|TokenStream
name|result
init|=
operator|new
name|SentenceTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|WordTokenizer
argument_list|(
name|result
argument_list|,
name|wordSegment
argument_list|)
expr_stmt|;
comment|// result = new LowerCaseFilter(result);
comment|// ä¸åéè¦LowerCaseFilterï¼å ä¸ºSegTokenFilterå·²ç»å°ææè±æå­ç¬¦è½¬æ¢æå°å
comment|// stemå¤ªä¸¥æ ¼äº, This is not bug, this feature:)
name|result
operator|=
operator|new
name|PorterStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * ä»åç¨è¯æä»¶ä¸­å è½½åç¨è¯ï¼ åç¨è¯æä»¶æ¯æ®éUTF-8ç¼ç çææ¬æä»¶ï¼ æ¯ä¸è¡æ¯ä¸ä¸ªåç¨è¯ï¼æ³¨éå©ç¨â//âï¼ åç¨è¯ä¸­åæ¬ä¸­ææ ç¹ç¬¦å·ï¼ ä¸­æç©ºæ ¼ï¼    * ä»¥åä½¿ç¨çå¤ªé«èå¯¹ç´¢å¼æä¹ä¸å¤§çè¯ã    *     * @param input åç¨è¯æä»¶    * @return åç¨è¯ç»æçHashSet    */
DECL|method|loadStopWords
specifier|public
specifier|static
name|Set
name|loadStopWords
parameter_list|(
name|InputStream
name|input
parameter_list|)
block|{
name|String
name|line
decl_stmt|;
name|Set
name|stopWords
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
try|try
block|{
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|input
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|"//"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|line
operator|.
name|indexOf
argument_list|(
literal|"//"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
name|stopWords
operator|.
name|add
argument_list|(
name|line
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: cannot open stop words list!"
argument_list|)
expr_stmt|;
block|}
return|return
name|stopWords
return|;
block|}
block|}
end_class

end_unit

