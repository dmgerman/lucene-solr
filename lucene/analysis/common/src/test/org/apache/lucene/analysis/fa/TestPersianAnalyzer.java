begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BaseTokenStreamTestCase
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
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_comment
comment|/**  * Test the Persian Analyzer  *   */
end_comment

begin_class
DECL|class|TestPersianAnalyzer
specifier|public
class|class
name|TestPersianAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * This test fails with NPE when the stopwords file is missing in classpath    */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|PersianAnalyzer
argument_list|()
expr_stmt|;
block|}
comment|/**    * This test shows how the combination of tokenization (breaking on zero-width    * non-joiner), normalization (such as treating arabic YEH and farsi YEH the    * same), and stopwords creates a light-stemming effect for verbs.    *     * These verb forms are from http://en.wikipedia.org/wiki/Persian_grammar    */
DECL|method|testBehaviorVerbs
specifier|public
name|void
name|testBehaviorVerbs
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|PersianAnalyzer
argument_list|()
decl_stmt|;
comment|// active present indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛâØ®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛâØ®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active future indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ§ÙØ¯ Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active present progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø±Ø¯ ÙÛâØ®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active preterite progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø´Øª ÙÛâØ®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯ÙâØ§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛâØ®ÙØ±Ø¯ÙâØ§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛâØ®ÙØ±Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛâØ®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛâØ®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive present indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÛâØ´ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯ÙâØ§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯ÙâØ§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive future indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø®ÙØ§ÙØ¯ Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive present progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø±Ø¯ Ø®ÙØ±Ø¯Ù ÙÛâØ´ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive preterite progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø´Øª Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive present subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÛâØ´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active present subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¨Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø¨Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test shows how the combination of tokenization and stopwords creates a    * light-stemming effect for verbs.    *     * In this case, these forms are presented with alternative orthography, using    * arabic yeh and whitespace. This yeh phenomenon is common for legacy text    * due to some previous bugs in Microsoft Windows.    *     * These verb forms are from http://en.wikipedia.org/wiki/Persian_grammar    */
DECL|method|testBehaviorVerbsDefective
specifier|public
name|void
name|testBehaviorVerbsDefective
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|PersianAnalyzer
argument_list|()
decl_stmt|;
comment|// active present indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÙ Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÙ Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active future indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ§ÙØ¯ Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active present progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø±Ø¯ ÙÙ Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active preterite progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø´Øª ÙÙ Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
comment|// active perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÙ Ø®ÙØ±Ø¯Ù Ø§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÙ Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÙ Ø®ÙØ±Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active imperfective pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÙ Ø®ÙØ±Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive present indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective preterite indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective perfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø§Ø³Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective pluperfect indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive future indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø®ÙØ§ÙØ¯ Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive present progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø±Ø¯ Ø®ÙØ±Ø¯Ù ÙÙ Ø´ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive preterite progressive indicative
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¯Ø§Ø´Øª Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive present subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´ÙØ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective preterite subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// passive imperfective pluperfect subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
comment|// active present subjunctive
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¨Ø®ÙØ±Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø¨Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test shows how the combination of tokenization (breaking on zero-width    * non-joiner or space) and stopwords creates a light-stemming effect for    * nouns, removing the plural -ha.    */
DECL|method|testBehaviorNouns
specifier|public
name|void
name|testBehaviorNouns
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|PersianAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¨Ø±Ú¯ ÙØ§"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø¨Ø±Ú¯"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¨Ø±Ú¯âÙØ§"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø¨Ø±Ú¯"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test showing that non-persian text is treated very much like SimpleAnalyzer    * (lowercased, etc)    */
DECL|method|testBehaviorNonPersian
specifier|public
name|void
name|testBehaviorNonPersian
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|PersianAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"English test."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"english"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Basic test ensuring that tokenStream works correctly.    */
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|PersianAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø®ÙØ±Ø¯Ù ÙÙ Ø´Ø¯Ù Ø¨ÙØ¯Ù Ø¨Ø§Ø´Ø¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø®ÙØ±Ø¯Ù"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¨Ø±Ú¯âÙØ§"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø¨Ø±Ú¯"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that custom stopwords work, and are not case-sensitive.    */
DECL|method|testCustomStopwords
specifier|public
name|void
name|testCustomStopwords
parameter_list|()
throws|throws
name|Exception
block|{
name|PersianAnalyzer
name|a
init|=
operator|new
name|PersianAnalyzer
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|asSet
argument_list|(
literal|"the"
argument_list|,
literal|"and"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"The quick brown fox."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|PersianAnalyzer
argument_list|()
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

