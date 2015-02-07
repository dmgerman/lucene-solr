begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Analyzer for Chinese, Japanese, and Korean, which indexes bigrams.   * This analyzer generates bigram terms, which are overlapping groups of two adjacent Han, Hiragana, Katakana, or Hangul characters.  *<p>  * Three analyzers are provided for Chinese, each of which treats Chinese text in a different way.  *<ul>  *<li>ChineseAnalyzer (in the analyzers/cn package): Index unigrams (individual Chinese characters) as a token.  *<li>CJKAnalyzer (in this package): Index bigrams (overlapping groups of two adjacent Chinese characters) as tokens.  *<li>SmartChineseAnalyzer (in the analyzers/smartcn package): Index words (attempt to segment Chinese text into words) as tokens.  *</ul>  *   * Example phraseï¼ "ææ¯ä¸­å½äºº"  *<ol>  *<li>ChineseAnalyzer: æï¼æ¯ï¼ä¸­ï¼å½ï¼äºº</li>  *<li>CJKAnalyzer: ææ¯ï¼æ¯ä¸­ï¼ä¸­å½ï¼å½äºº</li>  *<li>SmartChineseAnalyzer: æï¼æ¯ï¼ä¸­å½ï¼äºº</li>  *</ol>  *</p>  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
package|;
end_package

end_unit

