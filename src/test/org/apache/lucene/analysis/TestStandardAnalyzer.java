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
name|standard
operator|.
name|StandardAnalyzer
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestStandardAnalyzer
specifier|public
class|class
name|TestStandardAnalyzer
extends|extends
name|LuceneTestCase
block|{
DECL|field|a
specifier|private
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|expected
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expectedImages
parameter_list|,
name|String
index|[]
name|expectedTypes
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|expectedImages
argument_list|,
name|expectedTypes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expectedImages
parameter_list|,
name|String
index|[]
name|expectedTypes
parameter_list|,
name|int
index|[]
name|expectedPosIncrs
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
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
name|expectedImages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|t
init|=
name|ts
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedImages
index|[
name|i
index|]
argument_list|,
name|t
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedTypes
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedTypes
index|[
name|i
index|]
argument_list|,
name|t
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expectedPosIncrs
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedPosIncrs
index|[
name|i
index|]
argument_list|,
name|t
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNull
argument_list|(
name|ts
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMaxTermLength
specifier|public
name|void
name|testMaxTermLength
parameter_list|()
throws|throws
name|Exception
block|{
name|StandardAnalyzer
name|sa
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|sa
operator|.
name|setMaxTokenLength
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"ab cd toolong xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxTermLength2
specifier|public
name|void
name|testMaxTermLength2
parameter_list|()
throws|throws
name|Exception
block|{
name|StandardAnalyzer
name|sa
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"ab cd toolong xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"toolong"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|sa
operator|.
name|setMaxTokenLength
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"ab cd toolong xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|,
literal|null
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxTermLength3
specifier|public
name|void
name|testMaxTermLength3
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|255
index|]
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
literal|255
condition|;
name|i
operator|++
control|)
name|chars
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
name|String
name|longTerm
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ab cd "
operator|+
name|longTerm
operator|+
literal|" xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
name|longTerm
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ab cd "
operator|+
name|longTerm
operator|+
literal|"a xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlphanumeric
specifier|public
name|void
name|testAlphanumeric
parameter_list|()
throws|throws
name|Exception
block|{
comment|// alphanumeric tokens
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"B2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b2b"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2b"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnderscores
specifier|public
name|void
name|testUnderscores
parameter_list|()
throws|throws
name|Exception
block|{
comment|// underscores are delimiters, but not in email addresses (below)
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"word_having_underscore"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"having"
block|,
literal|"underscore"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"word_with_underscore_and_stopwords"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"underscore"
block|,
literal|"stopwords"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDelimiters
specifier|public
name|void
name|testDelimiters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// other delimiters: "-", "/", ","
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"some-dashed-phrase"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"some"
block|,
literal|"dashed"
block|,
literal|"phrase"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"dogs,chase,cats"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dogs"
block|,
literal|"chase"
block|,
literal|"cats"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ac/dc"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ac"
block|,
literal|"dc"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testApostrophes
specifier|public
name|void
name|testApostrophes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// internal apostrophes: O'Reilly, you're, O'Reilly's
comment|// possessives are actually removed by StardardFilter, not the tokenizer
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"O'Reilly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"o'reilly"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"you're"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"you're"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"she's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"she"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Jim's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jim"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"don't"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"don't"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"O'Reilly's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"o'reilly"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTSADash
specifier|public
name|void
name|testTSADash
parameter_list|()
throws|throws
name|Exception
block|{
comment|// t and s had been stopwords in Lucene<= 2.0, which made it impossible
comment|// to correctly search for these terms:
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s-class"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
block|,
literal|"class"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"t-com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|,
literal|"com"
block|}
argument_list|)
expr_stmt|;
comment|// 'a' is still a stopword:
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a-class"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"class"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompanyNames
specifier|public
name|void
name|testCompanyNames
parameter_list|()
throws|throws
name|Exception
block|{
comment|// company names
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AT&T"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"at&t"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Excite@Home"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"excite@home"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLucene1140
specifier|public
name|void
name|testLucene1140
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|StandardAnalyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"www.nutch.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should not throw an NPE and it did"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDomainNames
specifier|public
name|void
name|testDomainNames
parameter_list|()
throws|throws
name|Exception
block|{
comment|// domain names
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"www.nutch.org"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|)
expr_stmt|;
comment|//Notice the trailing .  See https://issues.apache.org/jira/browse/LUCENE-1068.
comment|//TODO: Remove in 3.x
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"www.nutch.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"wwwnutchorg"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ACRONYM>"
block|}
argument_list|)
expr_stmt|;
comment|// the following should be recognized as HOST. The code that sets replaceDepAcronym should be removed in the next release.
operator|(
operator|(
name|StandardAnalyzer
operator|)
name|a
operator|)
operator|.
name|setReplaceInvalidAcronym
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"www.nutch.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEMailAddresses
specifier|public
name|void
name|testEMailAddresses
parameter_list|()
throws|throws
name|Exception
block|{
comment|// email addresses, possibly with underscores, periods, etc
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"test@example.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test@example.com"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"first.lastname@example.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"first.lastname@example.com"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"first_lastname@example.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"first_lastname@example.com"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumeric
specifier|public
name|void
name|testNumeric
parameter_list|()
throws|throws
name|Exception
block|{
comment|// floating point, serial, model numbers, ip addresses, etc.
comment|// every other segment must have at least one digit
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"21.35"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"21.35"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"R2D2 C3PO"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"r2d2"
block|,
literal|"c3po"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"216.239.63.104"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"216.239.63.104"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"1-2-3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1-2-3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a1-b2-c3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1-b2-c3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a1-b-c3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1-b-c3"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTextWithNumbers
specifier|public
name|void
name|testTextWithNumbers
parameter_list|()
throws|throws
name|Exception
block|{
comment|// numbers
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"David has 5000 bones"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"david"
block|,
literal|"has"
block|,
literal|"5000"
block|,
literal|"bones"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testVariousText
specifier|public
name|void
name|testVariousText
parameter_list|()
throws|throws
name|Exception
block|{
comment|// various
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C embedded developers wanted"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|,
literal|"embedded"
block|,
literal|"developers"
block|,
literal|"wanted"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo bar FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo      bar .  FOO<> BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\"QUOTED\" word"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quoted"
block|,
literal|"word"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAcronyms
specifier|public
name|void
name|testAcronyms
parameter_list|()
throws|throws
name|Exception
block|{
comment|// acronyms have their dots stripped
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"U.S.A."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"usa"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCPlusPlusHash
specifier|public
name|void
name|testCPlusPlusHash
parameter_list|()
throws|throws
name|Exception
block|{
comment|// It would be nice to change the grammar in StandardTokenizer.jj to make "C#" and "C++" end up as tokens.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C++"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C#"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKorean
specifier|public
name|void
name|testKorean
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Korean words
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ìëíì¸ì íê¸ìëë¤"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ìëíì¸ì"
block|,
literal|"íê¸ìëë¤"
block|}
argument_list|)
expr_stmt|;
block|}
comment|// Compliance with the "old" JavaCC-based analyzer, see:
comment|// https://issues.apache.org/jira/browse/LUCENE-966#action_12516752
DECL|method|testComplianceFileName
specifier|public
name|void
name|testComplianceFileName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2004.jpg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2004.jpg"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericIncorrect
specifier|public
name|void
name|testComplianceNumericIncorrect
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"62.46"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"62.46"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericLong
specifier|public
name|void
name|testComplianceNumericLong
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"978-0-94045043-1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"978-0-94045043-1"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericFile
specifier|public
name|void
name|testComplianceNumericFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"78academyawards/rules/rule02.html"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"78academyawards/rules/rule02.html"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericWithUnderscores
specifier|public
name|void
name|testComplianceNumericWithUnderscores
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2006-03-11t082958z_01_ban130523_rtridst_0_ozabs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2006-03-11t082958z_01_ban130523_rtridst_0_ozabs"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericWithDash
specifier|public
name|void
name|testComplianceNumericWithDash
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"mid-20th"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mid-20th"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceManyTokens
specifier|public
name|void
name|testComplianceManyTokens
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"/money.cnn.com/magazines/fortune/fortune_archive/2007/03/19/8402357/index.htm "
operator|+
literal|"safari-0-sheikh-zayed-grand-mosque.jpg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"money.cnn.com"
block|,
literal|"magazines"
block|,
literal|"fortune"
block|,
literal|"fortune"
block|,
literal|"archive/2007/03/19/8402357"
block|,
literal|"index.htm"
block|,
literal|"safari-0-sheikh"
block|,
literal|"zayed"
block|,
literal|"grand"
block|,
literal|"mosque.jpg"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<NUM>"
block|,
literal|"<HOST>"
block|,
literal|"<NUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated this should be removed in the 3.0. */
DECL|method|testDeprecatedAcronyms
specifier|public
name|void
name|testDeprecatedAcronyms
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test backward compatibility for applications that require the old behavior.
comment|// this should be removed once replaceDepAcronym is removed.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"lucene.apache.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"luceneapacheorg"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ACRONYM>"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

