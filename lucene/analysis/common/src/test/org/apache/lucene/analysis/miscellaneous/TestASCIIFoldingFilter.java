begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|MockTokenizer
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
name|Tokenizer
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
name|core
operator|.
name|KeywordTokenizer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_class
DECL|class|TestASCIIFoldingFilter
specifier|public
class|class
name|TestASCIIFoldingFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Pop one input token's worth of tokens off the filter and verify that they are as expected.    */
DECL|method|assertNextTerms
name|void
name|assertNextTerms
parameter_list|(
name|String
name|expectedUnfolded
parameter_list|,
name|String
name|expectedFolded
parameter_list|,
name|ASCIIFoldingFilter
name|filter
parameter_list|,
name|CharTermAttribute
name|termAtt
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFolded
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|.
name|isPreserveOriginal
argument_list|()
operator|&&
operator|!
name|expectedUnfolded
operator|.
name|equals
argument_list|(
name|expectedFolded
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedUnfolded
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// testLain1Accents() is a copy of TestLatin1AccentFilter.testU().
DECL|method|testLatin1Accents
specifier|public
name|void
name|testLatin1Accents
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"Des mot clÃ©s Ã LA CHAÃNE Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ã Ä² Ã Ã"
operator|+
literal|" Ã Ã Ã Ã Ã Ã Å Ã Ã Ã Ã Ã Ã Å¸ Ã  Ã¡ Ã¢ Ã£ Ã¤ Ã¥ Ã¦ Ã§ Ã¨ Ã© Ãª Ã« Ã¬ Ã­ Ã® Ã¯ Ä³"
operator|+
literal|" Ã° Ã± Ã² Ã³ Ã´ Ãµ Ã¶ Ã¸ Å Ã Ã¾ Ã¹ Ãº Ã» Ã¼ Ã½ Ã¿ ï¬ ï¬"
argument_list|)
decl_stmt|;
name|ASCIIFoldingFilter
name|filter
init|=
operator|new
name|ASCIIFoldingFilter
argument_list|(
name|stream
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Des"
argument_list|,
literal|"Des"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"mot"
argument_list|,
literal|"mot"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"clÃ©s"
argument_list|,
literal|"cles"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"LA"
argument_list|,
literal|"LA"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"CHAÃNE"
argument_list|,
literal|"CHAINE"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"A"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"AE"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"C"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"E"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"I"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ä²"
argument_list|,
literal|"IJ"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"D"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"N"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"O"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Å"
argument_list|,
literal|"OE"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"TH"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"U"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"Y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Å¸"
argument_list|,
literal|"Y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã "
argument_list|,
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¡"
argument_list|,
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¢"
argument_list|,
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã£"
argument_list|,
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¤"
argument_list|,
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¥"
argument_list|,
literal|"a"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¦"
argument_list|,
literal|"ae"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã§"
argument_list|,
literal|"c"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¨"
argument_list|,
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã©"
argument_list|,
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ãª"
argument_list|,
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã«"
argument_list|,
literal|"e"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¬"
argument_list|,
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã­"
argument_list|,
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã®"
argument_list|,
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¯"
argument_list|,
literal|"i"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ä³"
argument_list|,
literal|"ij"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã°"
argument_list|,
literal|"d"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã±"
argument_list|,
literal|"n"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã²"
argument_list|,
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã³"
argument_list|,
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã´"
argument_list|,
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ãµ"
argument_list|,
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¶"
argument_list|,
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¸"
argument_list|,
literal|"o"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Å"
argument_list|,
literal|"oe"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã"
argument_list|,
literal|"ss"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¾"
argument_list|,
literal|"th"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¹"
argument_list|,
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ãº"
argument_list|,
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã»"
argument_list|,
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¼"
argument_list|,
literal|"u"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã½"
argument_list|,
literal|"y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"Ã¿"
argument_list|,
literal|"y"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"ï¬"
argument_list|,
literal|"fi"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertNextTerms
argument_list|(
literal|"ï¬"
argument_list|,
literal|"fl"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// The following Perl script generated the foldings[] array automatically
comment|// from ASCIIFoldingFilter.java:
comment|//
comment|//    ============== begin get.test.cases.pl ==============
comment|//
comment|//    use strict;
comment|//    use warnings;
comment|//
comment|//    my $file = "ASCIIFoldingFilter.java";
comment|//    my $output = "testcases.txt";
comment|//    my %codes = ();
comment|//    my $folded = '';
comment|//
comment|//    open IN, "<:utf8", $file || die "Error opening input file '$file': $!";
comment|//    open OUT, ">:utf8", $output || die "Error opening output file '$output': $!";
comment|//
comment|//    while (my $line =<IN>) {
comment|//      chomp($line);
comment|//      # case '\u0133': //<char><maybe URL> [ description ]
comment|//      if ($line =~ /case\s+'\\u(....)':.*\[([^\]]+)\]/) {
comment|//        my $code = $1;
comment|//        my $desc = $2;
comment|//        $codes{$code} = $desc;
comment|//      }
comment|//      # output[outputPos++] = 'A';
comment|//      elsif ($line =~ /output\[outputPos\+\+\] = '(.+)';/) {
comment|//        my $output_char = $1;
comment|//        $folded .= $output_char;
comment|//      }
comment|//      elsif ($line =~ /break;/&& length($folded)> 0) {
comment|//        my $first = 1;
comment|//        for my $code (sort { hex($a)<=> hex($b) } keys %codes) {
comment|//          my $desc = $codes{$code};
comment|//          print OUT '      ';
comment|//          print OUT '+ ' if (not $first);
comment|//          $first = 0;
comment|//          print OUT '"', chr(hex($code)), qq!"  // U+$code: $desc\n!;
comment|//        }
comment|//        print OUT qq!      ,"$folded", // Folded result\n\n!;
comment|//        %codes = ();
comment|//        $folded = '';
comment|//      }
comment|//    }
comment|//    close OUT;
comment|//
comment|//    ============== end get.test.cases.pl ==============
comment|//
DECL|method|testAllFoldings
specifier|public
name|void
name|testAllFoldings
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Alternating strings of:
comment|//   1. All non-ASCII characters to be folded, concatenated together as a
comment|//      single string.
comment|//   2. The string of ASCII characters to which each of the above
comment|//      characters should be folded.
name|String
index|[]
name|foldings
init|=
block|{
literal|"Ã"
comment|// U+00C0: LATIN CAPITAL LETTER A WITH GRAVE
operator|+
literal|"Ã"
comment|// U+00C1: LATIN CAPITAL LETTER A WITH ACUTE
operator|+
literal|"Ã"
comment|// U+00C2: LATIN CAPITAL LETTER A WITH CIRCUMFLEX
operator|+
literal|"Ã"
comment|// U+00C3: LATIN CAPITAL LETTER A WITH TILDE
operator|+
literal|"Ã"
comment|// U+00C4: LATIN CAPITAL LETTER A WITH DIAERESIS
operator|+
literal|"Ã"
comment|// U+00C5: LATIN CAPITAL LETTER A WITH RING ABOVE
operator|+
literal|"Ä"
comment|// U+0100: LATIN CAPITAL LETTER A WITH MACRON
operator|+
literal|"Ä"
comment|// U+0102: LATIN CAPITAL LETTER A WITH BREVE
operator|+
literal|"Ä"
comment|// U+0104: LATIN CAPITAL LETTER A WITH OGONEK
operator|+
literal|"Æ"
comment|// U+018F: LATIN CAPITAL LETTER SCHWA
operator|+
literal|"Ç"
comment|// U+01CD: LATIN CAPITAL LETTER A WITH CARON
operator|+
literal|"Ç"
comment|// U+01DE: LATIN CAPITAL LETTER A WITH DIAERESIS AND MACRON
operator|+
literal|"Ç "
comment|// U+01E0: LATIN CAPITAL LETTER A WITH DOT ABOVE AND MACRON
operator|+
literal|"Çº"
comment|// U+01FA: LATIN CAPITAL LETTER A WITH RING ABOVE AND ACUTE
operator|+
literal|"È"
comment|// U+0200: LATIN CAPITAL LETTER A WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0202: LATIN CAPITAL LETTER A WITH INVERTED BREVE
operator|+
literal|"È¦"
comment|// U+0226: LATIN CAPITAL LETTER A WITH DOT ABOVE
operator|+
literal|"Èº"
comment|// U+023A: LATIN CAPITAL LETTER A WITH STROKE
operator|+
literal|"á´"
comment|// U+1D00: LATIN LETTER SMALL CAPITAL A
operator|+
literal|"á¸"
comment|// U+1E00: LATIN CAPITAL LETTER A WITH RING BELOW
operator|+
literal|"áº "
comment|// U+1EA0: LATIN CAPITAL LETTER A WITH DOT BELOW
operator|+
literal|"áº¢"
comment|// U+1EA2: LATIN CAPITAL LETTER A WITH HOOK ABOVE
operator|+
literal|"áº¤"
comment|// U+1EA4: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE
operator|+
literal|"áº¦"
comment|// U+1EA6: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE
operator|+
literal|"áº¨"
comment|// U+1EA8: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE
operator|+
literal|"áºª"
comment|// U+1EAA: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE
operator|+
literal|"áº¬"
comment|// U+1EAC: LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW
operator|+
literal|"áº®"
comment|// U+1EAE: LATIN CAPITAL LETTER A WITH BREVE AND ACUTE
operator|+
literal|"áº°"
comment|// U+1EB0: LATIN CAPITAL LETTER A WITH BREVE AND GRAVE
operator|+
literal|"áº²"
comment|// U+1EB2: LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE
operator|+
literal|"áº´"
comment|// U+1EB4: LATIN CAPITAL LETTER A WITH BREVE AND TILDE
operator|+
literal|"áº¶"
comment|// U+1EB6: LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW
operator|+
literal|"â¶"
comment|// U+24B6: CIRCLED LATIN CAPITAL LETTER A
operator|+
literal|"ï¼¡"
comment|// U+FF21: FULLWIDTH LATIN CAPITAL LETTER A
block|,
literal|"A"
block|,
comment|// Folded result
literal|"Ã "
comment|// U+00E0: LATIN SMALL LETTER A WITH GRAVE
operator|+
literal|"Ã¡"
comment|// U+00E1: LATIN SMALL LETTER A WITH ACUTE
operator|+
literal|"Ã¢"
comment|// U+00E2: LATIN SMALL LETTER A WITH CIRCUMFLEX
operator|+
literal|"Ã£"
comment|// U+00E3: LATIN SMALL LETTER A WITH TILDE
operator|+
literal|"Ã¤"
comment|// U+00E4: LATIN SMALL LETTER A WITH DIAERESIS
operator|+
literal|"Ã¥"
comment|// U+00E5: LATIN SMALL LETTER A WITH RING ABOVE
operator|+
literal|"Ä"
comment|// U+0101: LATIN SMALL LETTER A WITH MACRON
operator|+
literal|"Ä"
comment|// U+0103: LATIN SMALL LETTER A WITH BREVE
operator|+
literal|"Ä"
comment|// U+0105: LATIN SMALL LETTER A WITH OGONEK
operator|+
literal|"Ç"
comment|// U+01CE: LATIN SMALL LETTER A WITH CARON
operator|+
literal|"Ç"
comment|// U+01DF: LATIN SMALL LETTER A WITH DIAERESIS AND MACRON
operator|+
literal|"Ç¡"
comment|// U+01E1: LATIN SMALL LETTER A WITH DOT ABOVE AND MACRON
operator|+
literal|"Ç»"
comment|// U+01FB: LATIN SMALL LETTER A WITH RING ABOVE AND ACUTE
operator|+
literal|"È"
comment|// U+0201: LATIN SMALL LETTER A WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0203: LATIN SMALL LETTER A WITH INVERTED BREVE
operator|+
literal|"È§"
comment|// U+0227: LATIN SMALL LETTER A WITH DOT ABOVE
operator|+
literal|"É"
comment|// U+0250: LATIN SMALL LETTER TURNED A
operator|+
literal|"É"
comment|// U+0259: LATIN SMALL LETTER SCHWA
operator|+
literal|"É"
comment|// U+025A: LATIN SMALL LETTER SCHWA WITH HOOK
operator|+
literal|"á¶"
comment|// U+1D8F: LATIN SMALL LETTER A WITH RETROFLEX HOOK
operator|+
literal|"á¸"
comment|// U+1E01: LATIN SMALL LETTER A WITH RING BELOW
operator|+
literal|"á¶"
comment|// U+1D95: LATIN SMALL LETTER SCHWA WITH RETROFLEX HOOK
operator|+
literal|"áº"
comment|// U+1E9A: LATIN SMALL LETTER A WITH RIGHT HALF RING
operator|+
literal|"áº¡"
comment|// U+1EA1: LATIN SMALL LETTER A WITH DOT BELOW
operator|+
literal|"áº£"
comment|// U+1EA3: LATIN SMALL LETTER A WITH HOOK ABOVE
operator|+
literal|"áº¥"
comment|// U+1EA5: LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE
operator|+
literal|"áº§"
comment|// U+1EA7: LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE
operator|+
literal|"áº©"
comment|// U+1EA9: LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE
operator|+
literal|"áº«"
comment|// U+1EAB: LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE
operator|+
literal|"áº­"
comment|// U+1EAD: LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW
operator|+
literal|"áº¯"
comment|// U+1EAF: LATIN SMALL LETTER A WITH BREVE AND ACUTE
operator|+
literal|"áº±"
comment|// U+1EB1: LATIN SMALL LETTER A WITH BREVE AND GRAVE
operator|+
literal|"áº³"
comment|// U+1EB3: LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE
operator|+
literal|"áºµ"
comment|// U+1EB5: LATIN SMALL LETTER A WITH BREVE AND TILDE
operator|+
literal|"áº·"
comment|// U+1EB7: LATIN SMALL LETTER A WITH BREVE AND DOT BELOW
operator|+
literal|"â"
comment|// U+2090: LATIN SUBSCRIPT SMALL LETTER A
operator|+
literal|"â"
comment|// U+2094: LATIN SUBSCRIPT SMALL LETTER SCHWA
operator|+
literal|"â"
comment|// U+24D0: CIRCLED LATIN SMALL LETTER A
operator|+
literal|"â±¥"
comment|// U+2C65: LATIN SMALL LETTER A WITH STROKE
operator|+
literal|"â±¯"
comment|// U+2C6F: LATIN CAPITAL LETTER TURNED A
operator|+
literal|"ï½"
comment|// U+FF41: FULLWIDTH LATIN SMALL LETTER A
block|,
literal|"a"
block|,
comment|// Folded result
literal|"ê²"
comment|// U+A732: LATIN CAPITAL LETTER AA
block|,
literal|"AA"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00C6: LATIN CAPITAL LETTER AE
operator|+
literal|"Ç¢"
comment|// U+01E2: LATIN CAPITAL LETTER AE WITH MACRON
operator|+
literal|"Ç¼"
comment|// U+01FC: LATIN CAPITAL LETTER AE WITH ACUTE
operator|+
literal|"á´"
comment|// U+1D01: LATIN LETTER SMALL CAPITAL AE
block|,
literal|"AE"
block|,
comment|// Folded result
literal|"ê´"
comment|// U+A734: LATIN CAPITAL LETTER AO
block|,
literal|"AO"
block|,
comment|// Folded result
literal|"ê¶"
comment|// U+A736: LATIN CAPITAL LETTER AU
block|,
literal|"AU"
block|,
comment|// Folded result
literal|"ê¸"
comment|// U+A738: LATIN CAPITAL LETTER AV
operator|+
literal|"êº"
comment|// U+A73A: LATIN CAPITAL LETTER AV WITH HORIZONTAL BAR
block|,
literal|"AV"
block|,
comment|// Folded result
literal|"ê¼"
comment|// U+A73C: LATIN CAPITAL LETTER AY
block|,
literal|"AY"
block|,
comment|// Folded result
literal|"â"
comment|// U+249C: PARENTHESIZED LATIN SMALL LETTER A
block|,
literal|"(a)"
block|,
comment|// Folded result
literal|"ê³"
comment|// U+A733: LATIN SMALL LETTER AA
block|,
literal|"aa"
block|,
comment|// Folded result
literal|"Ã¦"
comment|// U+00E6: LATIN SMALL LETTER AE
operator|+
literal|"Ç£"
comment|// U+01E3: LATIN SMALL LETTER AE WITH MACRON
operator|+
literal|"Ç½"
comment|// U+01FD: LATIN SMALL LETTER AE WITH ACUTE
operator|+
literal|"á´"
comment|// U+1D02: LATIN SMALL LETTER TURNED AE
block|,
literal|"ae"
block|,
comment|// Folded result
literal|"êµ"
comment|// U+A735: LATIN SMALL LETTER AO
block|,
literal|"ao"
block|,
comment|// Folded result
literal|"ê·"
comment|// U+A737: LATIN SMALL LETTER AU
block|,
literal|"au"
block|,
comment|// Folded result
literal|"ê¹"
comment|// U+A739: LATIN SMALL LETTER AV
operator|+
literal|"ê»"
comment|// U+A73B: LATIN SMALL LETTER AV WITH HORIZONTAL BAR
block|,
literal|"av"
block|,
comment|// Folded result
literal|"ê½"
comment|// U+A73D: LATIN SMALL LETTER AY
block|,
literal|"ay"
block|,
comment|// Folded result
literal|"Æ"
comment|// U+0181: LATIN CAPITAL LETTER B WITH HOOK
operator|+
literal|"Æ"
comment|// U+0182: LATIN CAPITAL LETTER B WITH TOPBAR
operator|+
literal|"É"
comment|// U+0243: LATIN CAPITAL LETTER B WITH STROKE
operator|+
literal|"Ê"
comment|// U+0299: LATIN LETTER SMALL CAPITAL B
operator|+
literal|"á´"
comment|// U+1D03: LATIN LETTER SMALL CAPITAL BARRED B
operator|+
literal|"á¸"
comment|// U+1E02: LATIN CAPITAL LETTER B WITH DOT ABOVE
operator|+
literal|"á¸"
comment|// U+1E04: LATIN CAPITAL LETTER B WITH DOT BELOW
operator|+
literal|"á¸"
comment|// U+1E06: LATIN CAPITAL LETTER B WITH LINE BELOW
operator|+
literal|"â·"
comment|// U+24B7: CIRCLED LATIN CAPITAL LETTER B
operator|+
literal|"ï¼¢"
comment|// U+FF22: FULLWIDTH LATIN CAPITAL LETTER B
block|,
literal|"B"
block|,
comment|// Folded result
literal|"Æ"
comment|// U+0180: LATIN SMALL LETTER B WITH STROKE
operator|+
literal|"Æ"
comment|// U+0183: LATIN SMALL LETTER B WITH TOPBAR
operator|+
literal|"É"
comment|// U+0253: LATIN SMALL LETTER B WITH HOOK
operator|+
literal|"áµ¬"
comment|// U+1D6C: LATIN SMALL LETTER B WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D80: LATIN SMALL LETTER B WITH PALATAL HOOK
operator|+
literal|"á¸"
comment|// U+1E03: LATIN SMALL LETTER B WITH DOT ABOVE
operator|+
literal|"á¸"
comment|// U+1E05: LATIN SMALL LETTER B WITH DOT BELOW
operator|+
literal|"á¸"
comment|// U+1E07: LATIN SMALL LETTER B WITH LINE BELOW
operator|+
literal|"â"
comment|// U+24D1: CIRCLED LATIN SMALL LETTER B
operator|+
literal|"ï½"
comment|// U+FF42: FULLWIDTH LATIN SMALL LETTER B
block|,
literal|"b"
block|,
comment|// Folded result
literal|"â"
comment|// U+249D: PARENTHESIZED LATIN SMALL LETTER B
block|,
literal|"(b)"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00C7: LATIN CAPITAL LETTER C WITH CEDILLA
operator|+
literal|"Ä"
comment|// U+0106: LATIN CAPITAL LETTER C WITH ACUTE
operator|+
literal|"Ä"
comment|// U+0108: LATIN CAPITAL LETTER C WITH CIRCUMFLEX
operator|+
literal|"Ä"
comment|// U+010A: LATIN CAPITAL LETTER C WITH DOT ABOVE
operator|+
literal|"Ä"
comment|// U+010C: LATIN CAPITAL LETTER C WITH CARON
operator|+
literal|"Æ"
comment|// U+0187: LATIN CAPITAL LETTER C WITH HOOK
operator|+
literal|"È»"
comment|// U+023B: LATIN CAPITAL LETTER C WITH STROKE
operator|+
literal|"Ê"
comment|// U+0297: LATIN LETTER STRETCHED C
operator|+
literal|"á´"
comment|// U+1D04: LATIN LETTER SMALL CAPITAL C
operator|+
literal|"á¸"
comment|// U+1E08: LATIN CAPITAL LETTER C WITH CEDILLA AND ACUTE
operator|+
literal|"â¸"
comment|// U+24B8: CIRCLED LATIN CAPITAL LETTER C
operator|+
literal|"ï¼£"
comment|// U+FF23: FULLWIDTH LATIN CAPITAL LETTER C
block|,
literal|"C"
block|,
comment|// Folded result
literal|"Ã§"
comment|// U+00E7: LATIN SMALL LETTER C WITH CEDILLA
operator|+
literal|"Ä"
comment|// U+0107: LATIN SMALL LETTER C WITH ACUTE
operator|+
literal|"Ä"
comment|// U+0109: LATIN SMALL LETTER C WITH CIRCUMFLEX
operator|+
literal|"Ä"
comment|// U+010B: LATIN SMALL LETTER C WITH DOT ABOVE
operator|+
literal|"Ä"
comment|// U+010D: LATIN SMALL LETTER C WITH CARON
operator|+
literal|"Æ"
comment|// U+0188: LATIN SMALL LETTER C WITH HOOK
operator|+
literal|"È¼"
comment|// U+023C: LATIN SMALL LETTER C WITH STROKE
operator|+
literal|"É"
comment|// U+0255: LATIN SMALL LETTER C WITH CURL
operator|+
literal|"á¸"
comment|// U+1E09: LATIN SMALL LETTER C WITH CEDILLA AND ACUTE
operator|+
literal|"â"
comment|// U+2184: LATIN SMALL LETTER REVERSED C
operator|+
literal|"â"
comment|// U+24D2: CIRCLED LATIN SMALL LETTER C
operator|+
literal|"ê¾"
comment|// U+A73E: LATIN CAPITAL LETTER REVERSED C WITH DOT
operator|+
literal|"ê¿"
comment|// U+A73F: LATIN SMALL LETTER REVERSED C WITH DOT
operator|+
literal|"ï½"
comment|// U+FF43: FULLWIDTH LATIN SMALL LETTER C
block|,
literal|"c"
block|,
comment|// Folded result
literal|"â"
comment|// U+249E: PARENTHESIZED LATIN SMALL LETTER C
block|,
literal|"(c)"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00D0: LATIN CAPITAL LETTER ETH
operator|+
literal|"Ä"
comment|// U+010E: LATIN CAPITAL LETTER D WITH CARON
operator|+
literal|"Ä"
comment|// U+0110: LATIN CAPITAL LETTER D WITH STROKE
operator|+
literal|"Æ"
comment|// U+0189: LATIN CAPITAL LETTER AFRICAN D
operator|+
literal|"Æ"
comment|// U+018A: LATIN CAPITAL LETTER D WITH HOOK
operator|+
literal|"Æ"
comment|// U+018B: LATIN CAPITAL LETTER D WITH TOPBAR
operator|+
literal|"á´"
comment|// U+1D05: LATIN LETTER SMALL CAPITAL D
operator|+
literal|"á´"
comment|// U+1D06: LATIN LETTER SMALL CAPITAL ETH
operator|+
literal|"á¸"
comment|// U+1E0A: LATIN CAPITAL LETTER D WITH DOT ABOVE
operator|+
literal|"á¸"
comment|// U+1E0C: LATIN CAPITAL LETTER D WITH DOT BELOW
operator|+
literal|"á¸"
comment|// U+1E0E: LATIN CAPITAL LETTER D WITH LINE BELOW
operator|+
literal|"á¸"
comment|// U+1E10: LATIN CAPITAL LETTER D WITH CEDILLA
operator|+
literal|"á¸"
comment|// U+1E12: LATIN CAPITAL LETTER D WITH CIRCUMFLEX BELOW
operator|+
literal|"â¹"
comment|// U+24B9: CIRCLED LATIN CAPITAL LETTER D
operator|+
literal|"ê¹"
comment|// U+A779: LATIN CAPITAL LETTER INSULAR D
operator|+
literal|"ï¼¤"
comment|// U+FF24: FULLWIDTH LATIN CAPITAL LETTER D
block|,
literal|"D"
block|,
comment|// Folded result
literal|"Ã°"
comment|// U+00F0: LATIN SMALL LETTER ETH
operator|+
literal|"Ä"
comment|// U+010F: LATIN SMALL LETTER D WITH CARON
operator|+
literal|"Ä"
comment|// U+0111: LATIN SMALL LETTER D WITH STROKE
operator|+
literal|"Æ"
comment|// U+018C: LATIN SMALL LETTER D WITH TOPBAR
operator|+
literal|"È¡"
comment|// U+0221: LATIN SMALL LETTER D WITH CURL
operator|+
literal|"É"
comment|// U+0256: LATIN SMALL LETTER D WITH TAIL
operator|+
literal|"É"
comment|// U+0257: LATIN SMALL LETTER D WITH HOOK
operator|+
literal|"áµ­"
comment|// U+1D6D: LATIN SMALL LETTER D WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D81: LATIN SMALL LETTER D WITH PALATAL HOOK
operator|+
literal|"á¶"
comment|// U+1D91: LATIN SMALL LETTER D WITH HOOK AND TAIL
operator|+
literal|"á¸"
comment|// U+1E0B: LATIN SMALL LETTER D WITH DOT ABOVE
operator|+
literal|"á¸"
comment|// U+1E0D: LATIN SMALL LETTER D WITH DOT BELOW
operator|+
literal|"á¸"
comment|// U+1E0F: LATIN SMALL LETTER D WITH LINE BELOW
operator|+
literal|"á¸"
comment|// U+1E11: LATIN SMALL LETTER D WITH CEDILLA
operator|+
literal|"á¸"
comment|// U+1E13: LATIN SMALL LETTER D WITH CIRCUMFLEX BELOW
operator|+
literal|"â"
comment|// U+24D3: CIRCLED LATIN SMALL LETTER D
operator|+
literal|"êº"
comment|// U+A77A: LATIN SMALL LETTER INSULAR D
operator|+
literal|"ï½"
comment|// U+FF44: FULLWIDTH LATIN SMALL LETTER D
block|,
literal|"d"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01C4: LATIN CAPITAL LETTER DZ WITH CARON
operator|+
literal|"Ç±"
comment|// U+01F1: LATIN CAPITAL LETTER DZ
block|,
literal|"DZ"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01C5: LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON
operator|+
literal|"Ç²"
comment|// U+01F2: LATIN CAPITAL LETTER D WITH SMALL LETTER Z
block|,
literal|"Dz"
block|,
comment|// Folded result
literal|"â"
comment|// U+249F: PARENTHESIZED LATIN SMALL LETTER D
block|,
literal|"(d)"
block|,
comment|// Folded result
literal|"È¸"
comment|// U+0238: LATIN SMALL LETTER DB DIGRAPH
block|,
literal|"db"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01C6: LATIN SMALL LETTER DZ WITH CARON
operator|+
literal|"Ç³"
comment|// U+01F3: LATIN SMALL LETTER DZ
operator|+
literal|"Ê£"
comment|// U+02A3: LATIN SMALL LETTER DZ DIGRAPH
operator|+
literal|"Ê¥"
comment|// U+02A5: LATIN SMALL LETTER DZ DIGRAPH WITH CURL
block|,
literal|"dz"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00C8: LATIN CAPITAL LETTER E WITH GRAVE
operator|+
literal|"Ã"
comment|// U+00C9: LATIN CAPITAL LETTER E WITH ACUTE
operator|+
literal|"Ã"
comment|// U+00CA: LATIN CAPITAL LETTER E WITH CIRCUMFLEX
operator|+
literal|"Ã"
comment|// U+00CB: LATIN CAPITAL LETTER E WITH DIAERESIS
operator|+
literal|"Ä"
comment|// U+0112: LATIN CAPITAL LETTER E WITH MACRON
operator|+
literal|"Ä"
comment|// U+0114: LATIN CAPITAL LETTER E WITH BREVE
operator|+
literal|"Ä"
comment|// U+0116: LATIN CAPITAL LETTER E WITH DOT ABOVE
operator|+
literal|"Ä"
comment|// U+0118: LATIN CAPITAL LETTER E WITH OGONEK
operator|+
literal|"Ä"
comment|// U+011A: LATIN CAPITAL LETTER E WITH CARON
operator|+
literal|"Æ"
comment|// U+018E: LATIN CAPITAL LETTER REVERSED E
operator|+
literal|"Æ"
comment|// U+0190: LATIN CAPITAL LETTER OPEN E
operator|+
literal|"È"
comment|// U+0204: LATIN CAPITAL LETTER E WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0206: LATIN CAPITAL LETTER E WITH INVERTED BREVE
operator|+
literal|"È¨"
comment|// U+0228: LATIN CAPITAL LETTER E WITH CEDILLA
operator|+
literal|"É"
comment|// U+0246: LATIN CAPITAL LETTER E WITH STROKE
operator|+
literal|"á´"
comment|// U+1D07: LATIN LETTER SMALL CAPITAL E
operator|+
literal|"á¸"
comment|// U+1E14: LATIN CAPITAL LETTER E WITH MACRON AND GRAVE
operator|+
literal|"á¸"
comment|// U+1E16: LATIN CAPITAL LETTER E WITH MACRON AND ACUTE
operator|+
literal|"á¸"
comment|// U+1E18: LATIN CAPITAL LETTER E WITH CIRCUMFLEX BELOW
operator|+
literal|"á¸"
comment|// U+1E1A: LATIN CAPITAL LETTER E WITH TILDE BELOW
operator|+
literal|"á¸"
comment|// U+1E1C: LATIN CAPITAL LETTER E WITH CEDILLA AND BREVE
operator|+
literal|"áº¸"
comment|// U+1EB8: LATIN CAPITAL LETTER E WITH DOT BELOW
operator|+
literal|"áºº"
comment|// U+1EBA: LATIN CAPITAL LETTER E WITH HOOK ABOVE
operator|+
literal|"áº¼"
comment|// U+1EBC: LATIN CAPITAL LETTER E WITH TILDE
operator|+
literal|"áº¾"
comment|// U+1EBE: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE
operator|+
literal|"á»"
comment|// U+1EC0: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE
operator|+
literal|"á»"
comment|// U+1EC2: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1EC4: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE
operator|+
literal|"á»"
comment|// U+1EC6: LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW
operator|+
literal|"âº"
comment|// U+24BA: CIRCLED LATIN CAPITAL LETTER E
operator|+
literal|"â±»"
comment|// U+2C7B: LATIN LETTER SMALL CAPITAL TURNED E
operator|+
literal|"ï¼¥"
comment|// U+FF25: FULLWIDTH LATIN CAPITAL LETTER E
block|,
literal|"E"
block|,
comment|// Folded result
literal|"Ã¨"
comment|// U+00E8: LATIN SMALL LETTER E WITH GRAVE
operator|+
literal|"Ã©"
comment|// U+00E9: LATIN SMALL LETTER E WITH ACUTE
operator|+
literal|"Ãª"
comment|// U+00EA: LATIN SMALL LETTER E WITH CIRCUMFLEX
operator|+
literal|"Ã«"
comment|// U+00EB: LATIN SMALL LETTER E WITH DIAERESIS
operator|+
literal|"Ä"
comment|// U+0113: LATIN SMALL LETTER E WITH MACRON
operator|+
literal|"Ä"
comment|// U+0115: LATIN SMALL LETTER E WITH BREVE
operator|+
literal|"Ä"
comment|// U+0117: LATIN SMALL LETTER E WITH DOT ABOVE
operator|+
literal|"Ä"
comment|// U+0119: LATIN SMALL LETTER E WITH OGONEK
operator|+
literal|"Ä"
comment|// U+011B: LATIN SMALL LETTER E WITH CARON
operator|+
literal|"Ç"
comment|// U+01DD: LATIN SMALL LETTER TURNED E
operator|+
literal|"È"
comment|// U+0205: LATIN SMALL LETTER E WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0207: LATIN SMALL LETTER E WITH INVERTED BREVE
operator|+
literal|"È©"
comment|// U+0229: LATIN SMALL LETTER E WITH CEDILLA
operator|+
literal|"É"
comment|// U+0247: LATIN SMALL LETTER E WITH STROKE
operator|+
literal|"É"
comment|// U+0258: LATIN SMALL LETTER REVERSED E
operator|+
literal|"É"
comment|// U+025B: LATIN SMALL LETTER OPEN E
operator|+
literal|"É"
comment|// U+025C: LATIN SMALL LETTER REVERSED OPEN E
operator|+
literal|"É"
comment|// U+025D: LATIN SMALL LETTER REVERSED OPEN E WITH HOOK
operator|+
literal|"É"
comment|// U+025E: LATIN SMALL LETTER CLOSED REVERSED OPEN E
operator|+
literal|"Ê"
comment|// U+029A: LATIN SMALL LETTER CLOSED OPEN E
operator|+
literal|"á´"
comment|// U+1D08: LATIN SMALL LETTER TURNED OPEN E
operator|+
literal|"á¶"
comment|// U+1D92: LATIN SMALL LETTER E WITH RETROFLEX HOOK
operator|+
literal|"á¶"
comment|// U+1D93: LATIN SMALL LETTER OPEN E WITH RETROFLEX HOOK
operator|+
literal|"á¶"
comment|// U+1D94: LATIN SMALL LETTER REVERSED OPEN E WITH RETROFLEX HOOK
operator|+
literal|"á¸"
comment|// U+1E15: LATIN SMALL LETTER E WITH MACRON AND GRAVE
operator|+
literal|"á¸"
comment|// U+1E17: LATIN SMALL LETTER E WITH MACRON AND ACUTE
operator|+
literal|"á¸"
comment|// U+1E19: LATIN SMALL LETTER E WITH CIRCUMFLEX BELOW
operator|+
literal|"á¸"
comment|// U+1E1B: LATIN SMALL LETTER E WITH TILDE BELOW
operator|+
literal|"á¸"
comment|// U+1E1D: LATIN SMALL LETTER E WITH CEDILLA AND BREVE
operator|+
literal|"áº¹"
comment|// U+1EB9: LATIN SMALL LETTER E WITH DOT BELOW
operator|+
literal|"áº»"
comment|// U+1EBB: LATIN SMALL LETTER E WITH HOOK ABOVE
operator|+
literal|"áº½"
comment|// U+1EBD: LATIN SMALL LETTER E WITH TILDE
operator|+
literal|"áº¿"
comment|// U+1EBF: LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE
operator|+
literal|"á»"
comment|// U+1EC1: LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE
operator|+
literal|"á»"
comment|// U+1EC3: LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1EC5: LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE
operator|+
literal|"á»"
comment|// U+1EC7: LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW
operator|+
literal|"â"
comment|// U+2091: LATIN SUBSCRIPT SMALL LETTER E
operator|+
literal|"â"
comment|// U+24D4: CIRCLED LATIN SMALL LETTER E
operator|+
literal|"â±¸"
comment|// U+2C78: LATIN SMALL LETTER E WITH NOTCH
operator|+
literal|"ï½"
comment|// U+FF45: FULLWIDTH LATIN SMALL LETTER E
block|,
literal|"e"
block|,
comment|// Folded result
literal|"â "
comment|// U+24A0: PARENTHESIZED LATIN SMALL LETTER E
block|,
literal|"(e)"
block|,
comment|// Folded result
literal|"Æ"
comment|// U+0191: LATIN CAPITAL LETTER F WITH HOOK
operator|+
literal|"á¸"
comment|// U+1E1E: LATIN CAPITAL LETTER F WITH DOT ABOVE
operator|+
literal|"â»"
comment|// U+24BB: CIRCLED LATIN CAPITAL LETTER F
operator|+
literal|"ê°"
comment|// U+A730: LATIN LETTER SMALL CAPITAL F
operator|+
literal|"ê»"
comment|// U+A77B: LATIN CAPITAL LETTER INSULAR F
operator|+
literal|"ê»"
comment|// U+A7FB: LATIN EPIGRAPHIC LETTER REVERSED F
operator|+
literal|"ï¼¦"
comment|// U+FF26: FULLWIDTH LATIN CAPITAL LETTER F
block|,
literal|"F"
block|,
comment|// Folded result
literal|"Æ"
comment|// U+0192: LATIN SMALL LETTER F WITH HOOK
operator|+
literal|"áµ®"
comment|// U+1D6E: LATIN SMALL LETTER F WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D82: LATIN SMALL LETTER F WITH PALATAL HOOK
operator|+
literal|"á¸"
comment|// U+1E1F: LATIN SMALL LETTER F WITH DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E9B: LATIN SMALL LETTER LONG S WITH DOT ABOVE
operator|+
literal|"â"
comment|// U+24D5: CIRCLED LATIN SMALL LETTER F
operator|+
literal|"ê¼"
comment|// U+A77C: LATIN SMALL LETTER INSULAR F
operator|+
literal|"ï½"
comment|// U+FF46: FULLWIDTH LATIN SMALL LETTER F
block|,
literal|"f"
block|,
comment|// Folded result
literal|"â¡"
comment|// U+24A1: PARENTHESIZED LATIN SMALL LETTER F
block|,
literal|"(f)"
block|,
comment|// Folded result
literal|"ï¬"
comment|// U+FB00: LATIN SMALL LIGATURE FF
block|,
literal|"ff"
block|,
comment|// Folded result
literal|"ï¬"
comment|// U+FB03: LATIN SMALL LIGATURE FFI
block|,
literal|"ffi"
block|,
comment|// Folded result
literal|"ï¬"
comment|// U+FB04: LATIN SMALL LIGATURE FFL
block|,
literal|"ffl"
block|,
comment|// Folded result
literal|"ï¬"
comment|// U+FB01: LATIN SMALL LIGATURE FI
block|,
literal|"fi"
block|,
comment|// Folded result
literal|"ï¬"
comment|// U+FB02: LATIN SMALL LIGATURE FL
block|,
literal|"fl"
block|,
comment|// Folded result
literal|"Ä"
comment|// U+011C: LATIN CAPITAL LETTER G WITH CIRCUMFLEX
operator|+
literal|"Ä"
comment|// U+011E: LATIN CAPITAL LETTER G WITH BREVE
operator|+
literal|"Ä "
comment|// U+0120: LATIN CAPITAL LETTER G WITH DOT ABOVE
operator|+
literal|"Ä¢"
comment|// U+0122: LATIN CAPITAL LETTER G WITH CEDILLA
operator|+
literal|"Æ"
comment|// U+0193: LATIN CAPITAL LETTER G WITH HOOK
operator|+
literal|"Ç¤"
comment|// U+01E4: LATIN CAPITAL LETTER G WITH STROKE
operator|+
literal|"Ç¥"
comment|// U+01E5: LATIN SMALL LETTER G WITH STROKE
operator|+
literal|"Ç¦"
comment|// U+01E6: LATIN CAPITAL LETTER G WITH CARON
operator|+
literal|"Ç§"
comment|// U+01E7: LATIN SMALL LETTER G WITH CARON
operator|+
literal|"Ç´"
comment|// U+01F4: LATIN CAPITAL LETTER G WITH ACUTE
operator|+
literal|"É¢"
comment|// U+0262: LATIN LETTER SMALL CAPITAL G
operator|+
literal|"Ê"
comment|// U+029B: LATIN LETTER SMALL CAPITAL G WITH HOOK
operator|+
literal|"á¸ "
comment|// U+1E20: LATIN CAPITAL LETTER G WITH MACRON
operator|+
literal|"â¼"
comment|// U+24BC: CIRCLED LATIN CAPITAL LETTER G
operator|+
literal|"ê½"
comment|// U+A77D: LATIN CAPITAL LETTER INSULAR G
operator|+
literal|"ê¾"
comment|// U+A77E: LATIN CAPITAL LETTER TURNED INSULAR G
operator|+
literal|"ï¼§"
comment|// U+FF27: FULLWIDTH LATIN CAPITAL LETTER G
block|,
literal|"G"
block|,
comment|// Folded result
literal|"Ä"
comment|// U+011D: LATIN SMALL LETTER G WITH CIRCUMFLEX
operator|+
literal|"Ä"
comment|// U+011F: LATIN SMALL LETTER G WITH BREVE
operator|+
literal|"Ä¡"
comment|// U+0121: LATIN SMALL LETTER G WITH DOT ABOVE
operator|+
literal|"Ä£"
comment|// U+0123: LATIN SMALL LETTER G WITH CEDILLA
operator|+
literal|"Çµ"
comment|// U+01F5: LATIN SMALL LETTER G WITH ACUTE
operator|+
literal|"É "
comment|// U+0260: LATIN SMALL LETTER G WITH HOOK
operator|+
literal|"É¡"
comment|// U+0261: LATIN SMALL LETTER SCRIPT G
operator|+
literal|"áµ·"
comment|// U+1D77: LATIN SMALL LETTER TURNED G
operator|+
literal|"áµ¹"
comment|// U+1D79: LATIN SMALL LETTER INSULAR G
operator|+
literal|"á¶"
comment|// U+1D83: LATIN SMALL LETTER G WITH PALATAL HOOK
operator|+
literal|"á¸¡"
comment|// U+1E21: LATIN SMALL LETTER G WITH MACRON
operator|+
literal|"â"
comment|// U+24D6: CIRCLED LATIN SMALL LETTER G
operator|+
literal|"ê¿"
comment|// U+A77F: LATIN SMALL LETTER TURNED INSULAR G
operator|+
literal|"ï½"
comment|// U+FF47: FULLWIDTH LATIN SMALL LETTER G
block|,
literal|"g"
block|,
comment|// Folded result
literal|"â¢"
comment|// U+24A2: PARENTHESIZED LATIN SMALL LETTER G
block|,
literal|"(g)"
block|,
comment|// Folded result
literal|"Ä¤"
comment|// U+0124: LATIN CAPITAL LETTER H WITH CIRCUMFLEX
operator|+
literal|"Ä¦"
comment|// U+0126: LATIN CAPITAL LETTER H WITH STROKE
operator|+
literal|"È"
comment|// U+021E: LATIN CAPITAL LETTER H WITH CARON
operator|+
literal|"Ê"
comment|// U+029C: LATIN LETTER SMALL CAPITAL H
operator|+
literal|"á¸¢"
comment|// U+1E22: LATIN CAPITAL LETTER H WITH DOT ABOVE
operator|+
literal|"á¸¤"
comment|// U+1E24: LATIN CAPITAL LETTER H WITH DOT BELOW
operator|+
literal|"á¸¦"
comment|// U+1E26: LATIN CAPITAL LETTER H WITH DIAERESIS
operator|+
literal|"á¸¨"
comment|// U+1E28: LATIN CAPITAL LETTER H WITH CEDILLA
operator|+
literal|"á¸ª"
comment|// U+1E2A: LATIN CAPITAL LETTER H WITH BREVE BELOW
operator|+
literal|"â½"
comment|// U+24BD: CIRCLED LATIN CAPITAL LETTER H
operator|+
literal|"â±§"
comment|// U+2C67: LATIN CAPITAL LETTER H WITH DESCENDER
operator|+
literal|"â±µ"
comment|// U+2C75: LATIN CAPITAL LETTER HALF H
operator|+
literal|"ï¼¨"
comment|// U+FF28: FULLWIDTH LATIN CAPITAL LETTER H
block|,
literal|"H"
block|,
comment|// Folded result
literal|"Ä¥"
comment|// U+0125: LATIN SMALL LETTER H WITH CIRCUMFLEX
operator|+
literal|"Ä§"
comment|// U+0127: LATIN SMALL LETTER H WITH STROKE
operator|+
literal|"È"
comment|// U+021F: LATIN SMALL LETTER H WITH CARON
operator|+
literal|"É¥"
comment|// U+0265: LATIN SMALL LETTER TURNED H
operator|+
literal|"É¦"
comment|// U+0266: LATIN SMALL LETTER H WITH HOOK
operator|+
literal|"Ê®"
comment|// U+02AE: LATIN SMALL LETTER TURNED H WITH FISHHOOK
operator|+
literal|"Ê¯"
comment|// U+02AF: LATIN SMALL LETTER TURNED H WITH FISHHOOK AND TAIL
operator|+
literal|"á¸£"
comment|// U+1E23: LATIN SMALL LETTER H WITH DOT ABOVE
operator|+
literal|"á¸¥"
comment|// U+1E25: LATIN SMALL LETTER H WITH DOT BELOW
operator|+
literal|"á¸§"
comment|// U+1E27: LATIN SMALL LETTER H WITH DIAERESIS
operator|+
literal|"á¸©"
comment|// U+1E29: LATIN SMALL LETTER H WITH CEDILLA
operator|+
literal|"á¸«"
comment|// U+1E2B: LATIN SMALL LETTER H WITH BREVE BELOW
operator|+
literal|"áº"
comment|// U+1E96: LATIN SMALL LETTER H WITH LINE BELOW
operator|+
literal|"â"
comment|// U+24D7: CIRCLED LATIN SMALL LETTER H
operator|+
literal|"â±¨"
comment|// U+2C68: LATIN SMALL LETTER H WITH DESCENDER
operator|+
literal|"â±¶"
comment|// U+2C76: LATIN SMALL LETTER HALF H
operator|+
literal|"ï½"
comment|// U+FF48: FULLWIDTH LATIN SMALL LETTER H
block|,
literal|"h"
block|,
comment|// Folded result
literal|"Ç¶"
comment|// U+01F6: LATIN CAPITAL LETTER HWAIR
block|,
literal|"HV"
block|,
comment|// Folded result
literal|"â£"
comment|// U+24A3: PARENTHESIZED LATIN SMALL LETTER H
block|,
literal|"(h)"
block|,
comment|// Folded result
literal|"Æ"
comment|// U+0195: LATIN SMALL LETTER HV
block|,
literal|"hv"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00CC: LATIN CAPITAL LETTER I WITH GRAVE
operator|+
literal|"Ã"
comment|// U+00CD: LATIN CAPITAL LETTER I WITH ACUTE
operator|+
literal|"Ã"
comment|// U+00CE: LATIN CAPITAL LETTER I WITH CIRCUMFLEX
operator|+
literal|"Ã"
comment|// U+00CF: LATIN CAPITAL LETTER I WITH DIAERESIS
operator|+
literal|"Ä¨"
comment|// U+0128: LATIN CAPITAL LETTER I WITH TILDE
operator|+
literal|"Äª"
comment|// U+012A: LATIN CAPITAL LETTER I WITH MACRON
operator|+
literal|"Ä¬"
comment|// U+012C: LATIN CAPITAL LETTER I WITH BREVE
operator|+
literal|"Ä®"
comment|// U+012E: LATIN CAPITAL LETTER I WITH OGONEK
operator|+
literal|"Ä°"
comment|// U+0130: LATIN CAPITAL LETTER I WITH DOT ABOVE
operator|+
literal|"Æ"
comment|// U+0196: LATIN CAPITAL LETTER IOTA
operator|+
literal|"Æ"
comment|// U+0197: LATIN CAPITAL LETTER I WITH STROKE
operator|+
literal|"Ç"
comment|// U+01CF: LATIN CAPITAL LETTER I WITH CARON
operator|+
literal|"È"
comment|// U+0208: LATIN CAPITAL LETTER I WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+020A: LATIN CAPITAL LETTER I WITH INVERTED BREVE
operator|+
literal|"Éª"
comment|// U+026A: LATIN LETTER SMALL CAPITAL I
operator|+
literal|"áµ»"
comment|// U+1D7B: LATIN SMALL CAPITAL LETTER I WITH STROKE
operator|+
literal|"á¸¬"
comment|// U+1E2C: LATIN CAPITAL LETTER I WITH TILDE BELOW
operator|+
literal|"á¸®"
comment|// U+1E2E: LATIN CAPITAL LETTER I WITH DIAERESIS AND ACUTE
operator|+
literal|"á»"
comment|// U+1EC8: LATIN CAPITAL LETTER I WITH HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1ECA: LATIN CAPITAL LETTER I WITH DOT BELOW
operator|+
literal|"â¾"
comment|// U+24BE: CIRCLED LATIN CAPITAL LETTER I
operator|+
literal|"ê¾"
comment|// U+A7FE: LATIN EPIGRAPHIC LETTER I LONGA
operator|+
literal|"ï¼©"
comment|// U+FF29: FULLWIDTH LATIN CAPITAL LETTER I
block|,
literal|"I"
block|,
comment|// Folded result
literal|"Ã¬"
comment|// U+00EC: LATIN SMALL LETTER I WITH GRAVE
operator|+
literal|"Ã­"
comment|// U+00ED: LATIN SMALL LETTER I WITH ACUTE
operator|+
literal|"Ã®"
comment|// U+00EE: LATIN SMALL LETTER I WITH CIRCUMFLEX
operator|+
literal|"Ã¯"
comment|// U+00EF: LATIN SMALL LETTER I WITH DIAERESIS
operator|+
literal|"Ä©"
comment|// U+0129: LATIN SMALL LETTER I WITH TILDE
operator|+
literal|"Ä«"
comment|// U+012B: LATIN SMALL LETTER I WITH MACRON
operator|+
literal|"Ä­"
comment|// U+012D: LATIN SMALL LETTER I WITH BREVE
operator|+
literal|"Ä¯"
comment|// U+012F: LATIN SMALL LETTER I WITH OGONEK
operator|+
literal|"Ä±"
comment|// U+0131: LATIN SMALL LETTER DOTLESS I
operator|+
literal|"Ç"
comment|// U+01D0: LATIN SMALL LETTER I WITH CARON
operator|+
literal|"È"
comment|// U+0209: LATIN SMALL LETTER I WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+020B: LATIN SMALL LETTER I WITH INVERTED BREVE
operator|+
literal|"É¨"
comment|// U+0268: LATIN SMALL LETTER I WITH STROKE
operator|+
literal|"á´"
comment|// U+1D09: LATIN SMALL LETTER TURNED I
operator|+
literal|"áµ¢"
comment|// U+1D62: LATIN SUBSCRIPT SMALL LETTER I
operator|+
literal|"áµ¼"
comment|// U+1D7C: LATIN SMALL LETTER IOTA WITH STROKE
operator|+
literal|"á¶"
comment|// U+1D96: LATIN SMALL LETTER I WITH RETROFLEX HOOK
operator|+
literal|"á¸­"
comment|// U+1E2D: LATIN SMALL LETTER I WITH TILDE BELOW
operator|+
literal|"á¸¯"
comment|// U+1E2F: LATIN SMALL LETTER I WITH DIAERESIS AND ACUTE
operator|+
literal|"á»"
comment|// U+1EC9: LATIN SMALL LETTER I WITH HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1ECB: LATIN SMALL LETTER I WITH DOT BELOW
operator|+
literal|"â±"
comment|// U+2071: SUPERSCRIPT LATIN SMALL LETTER I
operator|+
literal|"â"
comment|// U+24D8: CIRCLED LATIN SMALL LETTER I
operator|+
literal|"ï½"
comment|// U+FF49: FULLWIDTH LATIN SMALL LETTER I
block|,
literal|"i"
block|,
comment|// Folded result
literal|"Ä²"
comment|// U+0132: LATIN CAPITAL LIGATURE IJ
block|,
literal|"IJ"
block|,
comment|// Folded result
literal|"â¤"
comment|// U+24A4: PARENTHESIZED LATIN SMALL LETTER I
block|,
literal|"(i)"
block|,
comment|// Folded result
literal|"Ä³"
comment|// U+0133: LATIN SMALL LIGATURE IJ
block|,
literal|"ij"
block|,
comment|// Folded result
literal|"Ä´"
comment|// U+0134: LATIN CAPITAL LETTER J WITH CIRCUMFLEX
operator|+
literal|"É"
comment|// U+0248: LATIN CAPITAL LETTER J WITH STROKE
operator|+
literal|"á´"
comment|// U+1D0A: LATIN LETTER SMALL CAPITAL J
operator|+
literal|"â¿"
comment|// U+24BF: CIRCLED LATIN CAPITAL LETTER J
operator|+
literal|"ï¼ª"
comment|// U+FF2A: FULLWIDTH LATIN CAPITAL LETTER J
block|,
literal|"J"
block|,
comment|// Folded result
literal|"Äµ"
comment|// U+0135: LATIN SMALL LETTER J WITH CIRCUMFLEX
operator|+
literal|"Ç°"
comment|// U+01F0: LATIN SMALL LETTER J WITH CARON
operator|+
literal|"È·"
comment|// U+0237: LATIN SMALL LETTER DOTLESS J
operator|+
literal|"É"
comment|// U+0249: LATIN SMALL LETTER J WITH STROKE
operator|+
literal|"É"
comment|// U+025F: LATIN SMALL LETTER DOTLESS J WITH STROKE
operator|+
literal|"Ê"
comment|// U+0284: LATIN SMALL LETTER DOTLESS J WITH STROKE AND HOOK
operator|+
literal|"Ê"
comment|// U+029D: LATIN SMALL LETTER J WITH CROSSED-TAIL
operator|+
literal|"â"
comment|// U+24D9: CIRCLED LATIN SMALL LETTER J
operator|+
literal|"â±¼"
comment|// U+2C7C: LATIN SUBSCRIPT SMALL LETTER J
operator|+
literal|"ï½"
comment|// U+FF4A: FULLWIDTH LATIN SMALL LETTER J
block|,
literal|"j"
block|,
comment|// Folded result
literal|"â¥"
comment|// U+24A5: PARENTHESIZED LATIN SMALL LETTER J
block|,
literal|"(j)"
block|,
comment|// Folded result
literal|"Ä¶"
comment|// U+0136: LATIN CAPITAL LETTER K WITH CEDILLA
operator|+
literal|"Æ"
comment|// U+0198: LATIN CAPITAL LETTER K WITH HOOK
operator|+
literal|"Ç¨"
comment|// U+01E8: LATIN CAPITAL LETTER K WITH CARON
operator|+
literal|"á´"
comment|// U+1D0B: LATIN LETTER SMALL CAPITAL K
operator|+
literal|"á¸°"
comment|// U+1E30: LATIN CAPITAL LETTER K WITH ACUTE
operator|+
literal|"á¸²"
comment|// U+1E32: LATIN CAPITAL LETTER K WITH DOT BELOW
operator|+
literal|"á¸´"
comment|// U+1E34: LATIN CAPITAL LETTER K WITH LINE BELOW
operator|+
literal|"â"
comment|// U+24C0: CIRCLED LATIN CAPITAL LETTER K
operator|+
literal|"â±©"
comment|// U+2C69: LATIN CAPITAL LETTER K WITH DESCENDER
operator|+
literal|"ê"
comment|// U+A740: LATIN CAPITAL LETTER K WITH STROKE
operator|+
literal|"ê"
comment|// U+A742: LATIN CAPITAL LETTER K WITH DIAGONAL STROKE
operator|+
literal|"ê"
comment|// U+A744: LATIN CAPITAL LETTER K WITH STROKE AND DIAGONAL STROKE
operator|+
literal|"ï¼«"
comment|// U+FF2B: FULLWIDTH LATIN CAPITAL LETTER K
block|,
literal|"K"
block|,
comment|// Folded result
literal|"Ä·"
comment|// U+0137: LATIN SMALL LETTER K WITH CEDILLA
operator|+
literal|"Æ"
comment|// U+0199: LATIN SMALL LETTER K WITH HOOK
operator|+
literal|"Ç©"
comment|// U+01E9: LATIN SMALL LETTER K WITH CARON
operator|+
literal|"Ê"
comment|// U+029E: LATIN SMALL LETTER TURNED K
operator|+
literal|"á¶"
comment|// U+1D84: LATIN SMALL LETTER K WITH PALATAL HOOK
operator|+
literal|"á¸±"
comment|// U+1E31: LATIN SMALL LETTER K WITH ACUTE
operator|+
literal|"á¸³"
comment|// U+1E33: LATIN SMALL LETTER K WITH DOT BELOW
operator|+
literal|"á¸µ"
comment|// U+1E35: LATIN SMALL LETTER K WITH LINE BELOW
operator|+
literal|"â"
comment|// U+24DA: CIRCLED LATIN SMALL LETTER K
operator|+
literal|"â±ª"
comment|// U+2C6A: LATIN SMALL LETTER K WITH DESCENDER
operator|+
literal|"ê"
comment|// U+A741: LATIN SMALL LETTER K WITH STROKE
operator|+
literal|"ê"
comment|// U+A743: LATIN SMALL LETTER K WITH DIAGONAL STROKE
operator|+
literal|"ê"
comment|// U+A745: LATIN SMALL LETTER K WITH STROKE AND DIAGONAL STROKE
operator|+
literal|"ï½"
comment|// U+FF4B: FULLWIDTH LATIN SMALL LETTER K
block|,
literal|"k"
block|,
comment|// Folded result
literal|"â¦"
comment|// U+24A6: PARENTHESIZED LATIN SMALL LETTER K
block|,
literal|"(k)"
block|,
comment|// Folded result
literal|"Ä¹"
comment|// U+0139: LATIN CAPITAL LETTER L WITH ACUTE
operator|+
literal|"Ä»"
comment|// U+013B: LATIN CAPITAL LETTER L WITH CEDILLA
operator|+
literal|"Ä½"
comment|// U+013D: LATIN CAPITAL LETTER L WITH CARON
operator|+
literal|"Ä¿"
comment|// U+013F: LATIN CAPITAL LETTER L WITH MIDDLE DOT
operator|+
literal|"Å"
comment|// U+0141: LATIN CAPITAL LETTER L WITH STROKE
operator|+
literal|"È½"
comment|// U+023D: LATIN CAPITAL LETTER L WITH BAR
operator|+
literal|"Ê"
comment|// U+029F: LATIN LETTER SMALL CAPITAL L
operator|+
literal|"á´"
comment|// U+1D0C: LATIN LETTER SMALL CAPITAL L WITH STROKE
operator|+
literal|"á¸¶"
comment|// U+1E36: LATIN CAPITAL LETTER L WITH DOT BELOW
operator|+
literal|"á¸¸"
comment|// U+1E38: LATIN CAPITAL LETTER L WITH DOT BELOW AND MACRON
operator|+
literal|"á¸º"
comment|// U+1E3A: LATIN CAPITAL LETTER L WITH LINE BELOW
operator|+
literal|"á¸¼"
comment|// U+1E3C: LATIN CAPITAL LETTER L WITH CIRCUMFLEX BELOW
operator|+
literal|"â"
comment|// U+24C1: CIRCLED LATIN CAPITAL LETTER L
operator|+
literal|"â± "
comment|// U+2C60: LATIN CAPITAL LETTER L WITH DOUBLE BAR
operator|+
literal|"â±¢"
comment|// U+2C62: LATIN CAPITAL LETTER L WITH MIDDLE TILDE
operator|+
literal|"ê"
comment|// U+A746: LATIN CAPITAL LETTER BROKEN L
operator|+
literal|"ê"
comment|// U+A748: LATIN CAPITAL LETTER L WITH HIGH STROKE
operator|+
literal|"ê"
comment|// U+A780: LATIN CAPITAL LETTER TURNED L
operator|+
literal|"ï¼¬"
comment|// U+FF2C: FULLWIDTH LATIN CAPITAL LETTER L
block|,
literal|"L"
block|,
comment|// Folded result
literal|"Äº"
comment|// U+013A: LATIN SMALL LETTER L WITH ACUTE
operator|+
literal|"Ä¼"
comment|// U+013C: LATIN SMALL LETTER L WITH CEDILLA
operator|+
literal|"Ä¾"
comment|// U+013E: LATIN SMALL LETTER L WITH CARON
operator|+
literal|"Å"
comment|// U+0140: LATIN SMALL LETTER L WITH MIDDLE DOT
operator|+
literal|"Å"
comment|// U+0142: LATIN SMALL LETTER L WITH STROKE
operator|+
literal|"Æ"
comment|// U+019A: LATIN SMALL LETTER L WITH BAR
operator|+
literal|"È´"
comment|// U+0234: LATIN SMALL LETTER L WITH CURL
operator|+
literal|"É«"
comment|// U+026B: LATIN SMALL LETTER L WITH MIDDLE TILDE
operator|+
literal|"É¬"
comment|// U+026C: LATIN SMALL LETTER L WITH BELT
operator|+
literal|"É­"
comment|// U+026D: LATIN SMALL LETTER L WITH RETROFLEX HOOK
operator|+
literal|"á¶"
comment|// U+1D85: LATIN SMALL LETTER L WITH PALATAL HOOK
operator|+
literal|"á¸·"
comment|// U+1E37: LATIN SMALL LETTER L WITH DOT BELOW
operator|+
literal|"á¸¹"
comment|// U+1E39: LATIN SMALL LETTER L WITH DOT BELOW AND MACRON
operator|+
literal|"á¸»"
comment|// U+1E3B: LATIN SMALL LETTER L WITH LINE BELOW
operator|+
literal|"á¸½"
comment|// U+1E3D: LATIN SMALL LETTER L WITH CIRCUMFLEX BELOW
operator|+
literal|"â"
comment|// U+24DB: CIRCLED LATIN SMALL LETTER L
operator|+
literal|"â±¡"
comment|// U+2C61: LATIN SMALL LETTER L WITH DOUBLE BAR
operator|+
literal|"ê"
comment|// U+A747: LATIN SMALL LETTER BROKEN L
operator|+
literal|"ê"
comment|// U+A749: LATIN SMALL LETTER L WITH HIGH STROKE
operator|+
literal|"ê"
comment|// U+A781: LATIN SMALL LETTER TURNED L
operator|+
literal|"ï½"
comment|// U+FF4C: FULLWIDTH LATIN SMALL LETTER L
block|,
literal|"l"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01C7: LATIN CAPITAL LETTER LJ
block|,
literal|"LJ"
block|,
comment|// Folded result
literal|"á»º"
comment|// U+1EFA: LATIN CAPITAL LETTER MIDDLE-WELSH LL
block|,
literal|"LL"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01C8: LATIN CAPITAL LETTER L WITH SMALL LETTER J
block|,
literal|"Lj"
block|,
comment|// Folded result
literal|"â§"
comment|// U+24A7: PARENTHESIZED LATIN SMALL LETTER L
block|,
literal|"(l)"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01C9: LATIN SMALL LETTER LJ
block|,
literal|"lj"
block|,
comment|// Folded result
literal|"á»»"
comment|// U+1EFB: LATIN SMALL LETTER MIDDLE-WELSH LL
block|,
literal|"ll"
block|,
comment|// Folded result
literal|"Êª"
comment|// U+02AA: LATIN SMALL LETTER LS DIGRAPH
block|,
literal|"ls"
block|,
comment|// Folded result
literal|"Ê«"
comment|// U+02AB: LATIN SMALL LETTER LZ DIGRAPH
block|,
literal|"lz"
block|,
comment|// Folded result
literal|"Æ"
comment|// U+019C: LATIN CAPITAL LETTER TURNED M
operator|+
literal|"á´"
comment|// U+1D0D: LATIN LETTER SMALL CAPITAL M
operator|+
literal|"á¸¾"
comment|// U+1E3E: LATIN CAPITAL LETTER M WITH ACUTE
operator|+
literal|"á¹"
comment|// U+1E40: LATIN CAPITAL LETTER M WITH DOT ABOVE
operator|+
literal|"á¹"
comment|// U+1E42: LATIN CAPITAL LETTER M WITH DOT BELOW
operator|+
literal|"â"
comment|// U+24C2: CIRCLED LATIN CAPITAL LETTER M
operator|+
literal|"â±®"
comment|// U+2C6E: LATIN CAPITAL LETTER M WITH HOOK
operator|+
literal|"ê½"
comment|// U+A7FD: LATIN EPIGRAPHIC LETTER INVERTED M
operator|+
literal|"ê¿"
comment|// U+A7FF: LATIN EPIGRAPHIC LETTER ARCHAIC M
operator|+
literal|"ï¼­"
comment|// U+FF2D: FULLWIDTH LATIN CAPITAL LETTER M
block|,
literal|"M"
block|,
comment|// Folded result
literal|"É¯"
comment|// U+026F: LATIN SMALL LETTER TURNED M
operator|+
literal|"É°"
comment|// U+0270: LATIN SMALL LETTER TURNED M WITH LONG LEG
operator|+
literal|"É±"
comment|// U+0271: LATIN SMALL LETTER M WITH HOOK
operator|+
literal|"áµ¯"
comment|// U+1D6F: LATIN SMALL LETTER M WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D86: LATIN SMALL LETTER M WITH PALATAL HOOK
operator|+
literal|"á¸¿"
comment|// U+1E3F: LATIN SMALL LETTER M WITH ACUTE
operator|+
literal|"á¹"
comment|// U+1E41: LATIN SMALL LETTER M WITH DOT ABOVE
operator|+
literal|"á¹"
comment|// U+1E43: LATIN SMALL LETTER M WITH DOT BELOW
operator|+
literal|"â"
comment|// U+24DC: CIRCLED LATIN SMALL LETTER M
operator|+
literal|"ï½"
comment|// U+FF4D: FULLWIDTH LATIN SMALL LETTER M
block|,
literal|"m"
block|,
comment|// Folded result
literal|"â¨"
comment|// U+24A8: PARENTHESIZED LATIN SMALL LETTER M
block|,
literal|"(m)"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00D1: LATIN CAPITAL LETTER N WITH TILDE
operator|+
literal|"Å"
comment|// U+0143: LATIN CAPITAL LETTER N WITH ACUTE
operator|+
literal|"Å"
comment|// U+0145: LATIN CAPITAL LETTER N WITH CEDILLA
operator|+
literal|"Å"
comment|// U+0147: LATIN CAPITAL LETTER N WITH CARON
operator|+
literal|"Å"
comment|// U+014A: LATIN CAPITAL LETTER ENG
operator|+
literal|"Æ"
comment|// U+019D: LATIN CAPITAL LETTER N WITH LEFT HOOK
operator|+
literal|"Ç¸"
comment|// U+01F8: LATIN CAPITAL LETTER N WITH GRAVE
operator|+
literal|"È "
comment|// U+0220: LATIN CAPITAL LETTER N WITH LONG RIGHT LEG
operator|+
literal|"É´"
comment|// U+0274: LATIN LETTER SMALL CAPITAL N
operator|+
literal|"á´"
comment|// U+1D0E: LATIN LETTER SMALL CAPITAL REVERSED N
operator|+
literal|"á¹"
comment|// U+1E44: LATIN CAPITAL LETTER N WITH DOT ABOVE
operator|+
literal|"á¹"
comment|// U+1E46: LATIN CAPITAL LETTER N WITH DOT BELOW
operator|+
literal|"á¹"
comment|// U+1E48: LATIN CAPITAL LETTER N WITH LINE BELOW
operator|+
literal|"á¹"
comment|// U+1E4A: LATIN CAPITAL LETTER N WITH CIRCUMFLEX BELOW
operator|+
literal|"â"
comment|// U+24C3: CIRCLED LATIN CAPITAL LETTER N
operator|+
literal|"ï¼®"
comment|// U+FF2E: FULLWIDTH LATIN CAPITAL LETTER N
block|,
literal|"N"
block|,
comment|// Folded result
literal|"Ã±"
comment|// U+00F1: LATIN SMALL LETTER N WITH TILDE
operator|+
literal|"Å"
comment|// U+0144: LATIN SMALL LETTER N WITH ACUTE
operator|+
literal|"Å"
comment|// U+0146: LATIN SMALL LETTER N WITH CEDILLA
operator|+
literal|"Å"
comment|// U+0148: LATIN SMALL LETTER N WITH CARON
operator|+
literal|"Å"
comment|// U+0149: LATIN SMALL LETTER N PRECEDED BY APOSTROPHE
operator|+
literal|"Å"
comment|// U+014B: LATIN SMALL LETTER ENG
operator|+
literal|"Æ"
comment|// U+019E: LATIN SMALL LETTER N WITH LONG RIGHT LEG
operator|+
literal|"Ç¹"
comment|// U+01F9: LATIN SMALL LETTER N WITH GRAVE
operator|+
literal|"Èµ"
comment|// U+0235: LATIN SMALL LETTER N WITH CURL
operator|+
literal|"É²"
comment|// U+0272: LATIN SMALL LETTER N WITH LEFT HOOK
operator|+
literal|"É³"
comment|// U+0273: LATIN SMALL LETTER N WITH RETROFLEX HOOK
operator|+
literal|"áµ°"
comment|// U+1D70: LATIN SMALL LETTER N WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D87: LATIN SMALL LETTER N WITH PALATAL HOOK
operator|+
literal|"á¹"
comment|// U+1E45: LATIN SMALL LETTER N WITH DOT ABOVE
operator|+
literal|"á¹"
comment|// U+1E47: LATIN SMALL LETTER N WITH DOT BELOW
operator|+
literal|"á¹"
comment|// U+1E49: LATIN SMALL LETTER N WITH LINE BELOW
operator|+
literal|"á¹"
comment|// U+1E4B: LATIN SMALL LETTER N WITH CIRCUMFLEX BELOW
operator|+
literal|"â¿"
comment|// U+207F: SUPERSCRIPT LATIN SMALL LETTER N
operator|+
literal|"â"
comment|// U+24DD: CIRCLED LATIN SMALL LETTER N
operator|+
literal|"ï½"
comment|// U+FF4E: FULLWIDTH LATIN SMALL LETTER N
block|,
literal|"n"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01CA: LATIN CAPITAL LETTER NJ
block|,
literal|"NJ"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01CB: LATIN CAPITAL LETTER N WITH SMALL LETTER J
block|,
literal|"Nj"
block|,
comment|// Folded result
literal|"â©"
comment|// U+24A9: PARENTHESIZED LATIN SMALL LETTER N
block|,
literal|"(n)"
block|,
comment|// Folded result
literal|"Ç"
comment|// U+01CC: LATIN SMALL LETTER NJ
block|,
literal|"nj"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00D2: LATIN CAPITAL LETTER O WITH GRAVE
operator|+
literal|"Ã"
comment|// U+00D3: LATIN CAPITAL LETTER O WITH ACUTE
operator|+
literal|"Ã"
comment|// U+00D4: LATIN CAPITAL LETTER O WITH CIRCUMFLEX
operator|+
literal|"Ã"
comment|// U+00D5: LATIN CAPITAL LETTER O WITH TILDE
operator|+
literal|"Ã"
comment|// U+00D6: LATIN CAPITAL LETTER O WITH DIAERESIS
operator|+
literal|"Ã"
comment|// U+00D8: LATIN CAPITAL LETTER O WITH STROKE
operator|+
literal|"Å"
comment|// U+014C: LATIN CAPITAL LETTER O WITH MACRON
operator|+
literal|"Å"
comment|// U+014E: LATIN CAPITAL LETTER O WITH BREVE
operator|+
literal|"Å"
comment|// U+0150: LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
operator|+
literal|"Æ"
comment|// U+0186: LATIN CAPITAL LETTER OPEN O
operator|+
literal|"Æ"
comment|// U+019F: LATIN CAPITAL LETTER O WITH MIDDLE TILDE
operator|+
literal|"Æ "
comment|// U+01A0: LATIN CAPITAL LETTER O WITH HORN
operator|+
literal|"Ç"
comment|// U+01D1: LATIN CAPITAL LETTER O WITH CARON
operator|+
literal|"Çª"
comment|// U+01EA: LATIN CAPITAL LETTER O WITH OGONEK
operator|+
literal|"Ç¬"
comment|// U+01EC: LATIN CAPITAL LETTER O WITH OGONEK AND MACRON
operator|+
literal|"Ç¾"
comment|// U+01FE: LATIN CAPITAL LETTER O WITH STROKE AND ACUTE
operator|+
literal|"È"
comment|// U+020C: LATIN CAPITAL LETTER O WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+020E: LATIN CAPITAL LETTER O WITH INVERTED BREVE
operator|+
literal|"Èª"
comment|// U+022A: LATIN CAPITAL LETTER O WITH DIAERESIS AND MACRON
operator|+
literal|"È¬"
comment|// U+022C: LATIN CAPITAL LETTER O WITH TILDE AND MACRON
operator|+
literal|"È®"
comment|// U+022E: LATIN CAPITAL LETTER O WITH DOT ABOVE
operator|+
literal|"È°"
comment|// U+0230: LATIN CAPITAL LETTER O WITH DOT ABOVE AND MACRON
operator|+
literal|"á´"
comment|// U+1D0F: LATIN LETTER SMALL CAPITAL O
operator|+
literal|"á´"
comment|// U+1D10: LATIN LETTER SMALL CAPITAL OPEN O
operator|+
literal|"á¹"
comment|// U+1E4C: LATIN CAPITAL LETTER O WITH TILDE AND ACUTE
operator|+
literal|"á¹"
comment|// U+1E4E: LATIN CAPITAL LETTER O WITH TILDE AND DIAERESIS
operator|+
literal|"á¹"
comment|// U+1E50: LATIN CAPITAL LETTER O WITH MACRON AND GRAVE
operator|+
literal|"á¹"
comment|// U+1E52: LATIN CAPITAL LETTER O WITH MACRON AND ACUTE
operator|+
literal|"á»"
comment|// U+1ECC: LATIN CAPITAL LETTER O WITH DOT BELOW
operator|+
literal|"á»"
comment|// U+1ECE: LATIN CAPITAL LETTER O WITH HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1ED0: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE
operator|+
literal|"á»"
comment|// U+1ED2: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE
operator|+
literal|"á»"
comment|// U+1ED4: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1ED6: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE
operator|+
literal|"á»"
comment|// U+1ED8: LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW
operator|+
literal|"á»"
comment|// U+1EDA: LATIN CAPITAL LETTER O WITH HORN AND ACUTE
operator|+
literal|"á»"
comment|// U+1EDC: LATIN CAPITAL LETTER O WITH HORN AND GRAVE
operator|+
literal|"á»"
comment|// U+1EDE: LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE
operator|+
literal|"á» "
comment|// U+1EE0: LATIN CAPITAL LETTER O WITH HORN AND TILDE
operator|+
literal|"á»¢"
comment|// U+1EE2: LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW
operator|+
literal|"â"
comment|// U+24C4: CIRCLED LATIN CAPITAL LETTER O
operator|+
literal|"ê"
comment|// U+A74A: LATIN CAPITAL LETTER O WITH LONG STROKE OVERLAY
operator|+
literal|"ê"
comment|// U+A74C: LATIN CAPITAL LETTER O WITH LOOP
operator|+
literal|"ï¼¯"
comment|// U+FF2F: FULLWIDTH LATIN CAPITAL LETTER O
block|,
literal|"O"
block|,
comment|// Folded result
literal|"Ã²"
comment|// U+00F2: LATIN SMALL LETTER O WITH GRAVE
operator|+
literal|"Ã³"
comment|// U+00F3: LATIN SMALL LETTER O WITH ACUTE
operator|+
literal|"Ã´"
comment|// U+00F4: LATIN SMALL LETTER O WITH CIRCUMFLEX
operator|+
literal|"Ãµ"
comment|// U+00F5: LATIN SMALL LETTER O WITH TILDE
operator|+
literal|"Ã¶"
comment|// U+00F6: LATIN SMALL LETTER O WITH DIAERESIS
operator|+
literal|"Ã¸"
comment|// U+00F8: LATIN SMALL LETTER O WITH STROKE
operator|+
literal|"Å"
comment|// U+014D: LATIN SMALL LETTER O WITH MACRON
operator|+
literal|"Å"
comment|// U+014F: LATIN SMALL LETTER O WITH BREVE
operator|+
literal|"Å"
comment|// U+0151: LATIN SMALL LETTER O WITH DOUBLE ACUTE
operator|+
literal|"Æ¡"
comment|// U+01A1: LATIN SMALL LETTER O WITH HORN
operator|+
literal|"Ç"
comment|// U+01D2: LATIN SMALL LETTER O WITH CARON
operator|+
literal|"Ç«"
comment|// U+01EB: LATIN SMALL LETTER O WITH OGONEK
operator|+
literal|"Ç­"
comment|// U+01ED: LATIN SMALL LETTER O WITH OGONEK AND MACRON
operator|+
literal|"Ç¿"
comment|// U+01FF: LATIN SMALL LETTER O WITH STROKE AND ACUTE
operator|+
literal|"È"
comment|// U+020D: LATIN SMALL LETTER O WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+020F: LATIN SMALL LETTER O WITH INVERTED BREVE
operator|+
literal|"È«"
comment|// U+022B: LATIN SMALL LETTER O WITH DIAERESIS AND MACRON
operator|+
literal|"È­"
comment|// U+022D: LATIN SMALL LETTER O WITH TILDE AND MACRON
operator|+
literal|"È¯"
comment|// U+022F: LATIN SMALL LETTER O WITH DOT ABOVE
operator|+
literal|"È±"
comment|// U+0231: LATIN SMALL LETTER O WITH DOT ABOVE AND MACRON
operator|+
literal|"É"
comment|// U+0254: LATIN SMALL LETTER OPEN O
operator|+
literal|"Éµ"
comment|// U+0275: LATIN SMALL LETTER BARRED O
operator|+
literal|"á´"
comment|// U+1D16: LATIN SMALL LETTER TOP HALF O
operator|+
literal|"á´"
comment|// U+1D17: LATIN SMALL LETTER BOTTOM HALF O
operator|+
literal|"á¶"
comment|// U+1D97: LATIN SMALL LETTER OPEN O WITH RETROFLEX HOOK
operator|+
literal|"á¹"
comment|// U+1E4D: LATIN SMALL LETTER O WITH TILDE AND ACUTE
operator|+
literal|"á¹"
comment|// U+1E4F: LATIN SMALL LETTER O WITH TILDE AND DIAERESIS
operator|+
literal|"á¹"
comment|// U+1E51: LATIN SMALL LETTER O WITH MACRON AND GRAVE
operator|+
literal|"á¹"
comment|// U+1E53: LATIN SMALL LETTER O WITH MACRON AND ACUTE
operator|+
literal|"á»"
comment|// U+1ECD: LATIN SMALL LETTER O WITH DOT BELOW
operator|+
literal|"á»"
comment|// U+1ECF: LATIN SMALL LETTER O WITH HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1ED1: LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE
operator|+
literal|"á»"
comment|// U+1ED3: LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE
operator|+
literal|"á»"
comment|// U+1ED5: LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE
operator|+
literal|"á»"
comment|// U+1ED7: LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE
operator|+
literal|"á»"
comment|// U+1ED9: LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW
operator|+
literal|"á»"
comment|// U+1EDB: LATIN SMALL LETTER O WITH HORN AND ACUTE
operator|+
literal|"á»"
comment|// U+1EDD: LATIN SMALL LETTER O WITH HORN AND GRAVE
operator|+
literal|"á»"
comment|// U+1EDF: LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE
operator|+
literal|"á»¡"
comment|// U+1EE1: LATIN SMALL LETTER O WITH HORN AND TILDE
operator|+
literal|"á»£"
comment|// U+1EE3: LATIN SMALL LETTER O WITH HORN AND DOT BELOW
operator|+
literal|"â"
comment|// U+2092: LATIN SUBSCRIPT SMALL LETTER O
operator|+
literal|"â"
comment|// U+24DE: CIRCLED LATIN SMALL LETTER O
operator|+
literal|"â±º"
comment|// U+2C7A: LATIN SMALL LETTER O WITH LOW RING INSIDE
operator|+
literal|"ê"
comment|// U+A74B: LATIN SMALL LETTER O WITH LONG STROKE OVERLAY
operator|+
literal|"ê"
comment|// U+A74D: LATIN SMALL LETTER O WITH LOOP
operator|+
literal|"ï½"
comment|// U+FF4F: FULLWIDTH LATIN SMALL LETTER O
block|,
literal|"o"
block|,
comment|// Folded result
literal|"Å"
comment|// U+0152: LATIN CAPITAL LIGATURE OE
operator|+
literal|"É¶"
comment|// U+0276: LATIN LETTER SMALL CAPITAL OE
block|,
literal|"OE"
block|,
comment|// Folded result
literal|"ê"
comment|// U+A74E: LATIN CAPITAL LETTER OO
block|,
literal|"OO"
block|,
comment|// Folded result
literal|"È¢"
comment|// U+0222: LATIN CAPITAL LETTER OU
operator|+
literal|"á´"
comment|// U+1D15: LATIN LETTER SMALL CAPITAL OU
block|,
literal|"OU"
block|,
comment|// Folded result
literal|"âª"
comment|// U+24AA: PARENTHESIZED LATIN SMALL LETTER O
block|,
literal|"(o)"
block|,
comment|// Folded result
literal|"Å"
comment|// U+0153: LATIN SMALL LIGATURE OE
operator|+
literal|"á´"
comment|// U+1D14: LATIN SMALL LETTER TURNED OE
block|,
literal|"oe"
block|,
comment|// Folded result
literal|"ê"
comment|// U+A74F: LATIN SMALL LETTER OO
block|,
literal|"oo"
block|,
comment|// Folded result
literal|"È£"
comment|// U+0223: LATIN SMALL LETTER OU
block|,
literal|"ou"
block|,
comment|// Folded result
literal|"Æ¤"
comment|// U+01A4: LATIN CAPITAL LETTER P WITH HOOK
operator|+
literal|"á´"
comment|// U+1D18: LATIN LETTER SMALL CAPITAL P
operator|+
literal|"á¹"
comment|// U+1E54: LATIN CAPITAL LETTER P WITH ACUTE
operator|+
literal|"á¹"
comment|// U+1E56: LATIN CAPITAL LETTER P WITH DOT ABOVE
operator|+
literal|"â"
comment|// U+24C5: CIRCLED LATIN CAPITAL LETTER P
operator|+
literal|"â±£"
comment|// U+2C63: LATIN CAPITAL LETTER P WITH STROKE
operator|+
literal|"ê"
comment|// U+A750: LATIN CAPITAL LETTER P WITH STROKE THROUGH DESCENDER
operator|+
literal|"ê"
comment|// U+A752: LATIN CAPITAL LETTER P WITH FLOURISH
operator|+
literal|"ê"
comment|// U+A754: LATIN CAPITAL LETTER P WITH SQUIRREL TAIL
operator|+
literal|"ï¼°"
comment|// U+FF30: FULLWIDTH LATIN CAPITAL LETTER P
block|,
literal|"P"
block|,
comment|// Folded result
literal|"Æ¥"
comment|// U+01A5: LATIN SMALL LETTER P WITH HOOK
operator|+
literal|"áµ±"
comment|// U+1D71: LATIN SMALL LETTER P WITH MIDDLE TILDE
operator|+
literal|"áµ½"
comment|// U+1D7D: LATIN SMALL LETTER P WITH STROKE
operator|+
literal|"á¶"
comment|// U+1D88: LATIN SMALL LETTER P WITH PALATAL HOOK
operator|+
literal|"á¹"
comment|// U+1E55: LATIN SMALL LETTER P WITH ACUTE
operator|+
literal|"á¹"
comment|// U+1E57: LATIN SMALL LETTER P WITH DOT ABOVE
operator|+
literal|"â"
comment|// U+24DF: CIRCLED LATIN SMALL LETTER P
operator|+
literal|"ê"
comment|// U+A751: LATIN SMALL LETTER P WITH STROKE THROUGH DESCENDER
operator|+
literal|"ê"
comment|// U+A753: LATIN SMALL LETTER P WITH FLOURISH
operator|+
literal|"ê"
comment|// U+A755: LATIN SMALL LETTER P WITH SQUIRREL TAIL
operator|+
literal|"ê¼"
comment|// U+A7FC: LATIN EPIGRAPHIC LETTER REVERSED P
operator|+
literal|"ï½"
comment|// U+FF50: FULLWIDTH LATIN SMALL LETTER P
block|,
literal|"p"
block|,
comment|// Folded result
literal|"â«"
comment|// U+24AB: PARENTHESIZED LATIN SMALL LETTER P
block|,
literal|"(p)"
block|,
comment|// Folded result
literal|"É"
comment|// U+024A: LATIN CAPITAL LETTER SMALL Q WITH HOOK TAIL
operator|+
literal|"â"
comment|// U+24C6: CIRCLED LATIN CAPITAL LETTER Q
operator|+
literal|"ê"
comment|// U+A756: LATIN CAPITAL LETTER Q WITH STROKE THROUGH DESCENDER
operator|+
literal|"ê"
comment|// U+A758: LATIN CAPITAL LETTER Q WITH DIAGONAL STROKE
operator|+
literal|"ï¼±"
comment|// U+FF31: FULLWIDTH LATIN CAPITAL LETTER Q
block|,
literal|"Q"
block|,
comment|// Folded result
literal|"Ä¸"
comment|// U+0138: LATIN SMALL LETTER KRA
operator|+
literal|"É"
comment|// U+024B: LATIN SMALL LETTER Q WITH HOOK TAIL
operator|+
literal|"Ê "
comment|// U+02A0: LATIN SMALL LETTER Q WITH HOOK
operator|+
literal|"â "
comment|// U+24E0: CIRCLED LATIN SMALL LETTER Q
operator|+
literal|"ê"
comment|// U+A757: LATIN SMALL LETTER Q WITH STROKE THROUGH DESCENDER
operator|+
literal|"ê"
comment|// U+A759: LATIN SMALL LETTER Q WITH DIAGONAL STROKE
operator|+
literal|"ï½"
comment|// U+FF51: FULLWIDTH LATIN SMALL LETTER Q
block|,
literal|"q"
block|,
comment|// Folded result
literal|"â¬"
comment|// U+24AC: PARENTHESIZED LATIN SMALL LETTER Q
block|,
literal|"(q)"
block|,
comment|// Folded result
literal|"È¹"
comment|// U+0239: LATIN SMALL LETTER QP DIGRAPH
block|,
literal|"qp"
block|,
comment|// Folded result
literal|"Å"
comment|// U+0154: LATIN CAPITAL LETTER R WITH ACUTE
operator|+
literal|"Å"
comment|// U+0156: LATIN CAPITAL LETTER R WITH CEDILLA
operator|+
literal|"Å"
comment|// U+0158: LATIN CAPITAL LETTER R WITH CARON
operator|+
literal|"È"
comment|// U+0210: LATIN CAPITAL LETTER R WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0212: LATIN CAPITAL LETTER R WITH INVERTED BREVE
operator|+
literal|"É"
comment|// U+024C: LATIN CAPITAL LETTER R WITH STROKE
operator|+
literal|"Ê"
comment|// U+0280: LATIN LETTER SMALL CAPITAL R
operator|+
literal|"Ê"
comment|// U+0281: LATIN LETTER SMALL CAPITAL INVERTED R
operator|+
literal|"á´"
comment|// U+1D19: LATIN LETTER SMALL CAPITAL REVERSED R
operator|+
literal|"á´"
comment|// U+1D1A: LATIN LETTER SMALL CAPITAL TURNED R
operator|+
literal|"á¹"
comment|// U+1E58: LATIN CAPITAL LETTER R WITH DOT ABOVE
operator|+
literal|"á¹"
comment|// U+1E5A: LATIN CAPITAL LETTER R WITH DOT BELOW
operator|+
literal|"á¹"
comment|// U+1E5C: LATIN CAPITAL LETTER R WITH DOT BELOW AND MACRON
operator|+
literal|"á¹"
comment|// U+1E5E: LATIN CAPITAL LETTER R WITH LINE BELOW
operator|+
literal|"â"
comment|// U+24C7: CIRCLED LATIN CAPITAL LETTER R
operator|+
literal|"â±¤"
comment|// U+2C64: LATIN CAPITAL LETTER R WITH TAIL
operator|+
literal|"ê"
comment|// U+A75A: LATIN CAPITAL LETTER R ROTUNDA
operator|+
literal|"ê"
comment|// U+A782: LATIN CAPITAL LETTER INSULAR R
operator|+
literal|"ï¼²"
comment|// U+FF32: FULLWIDTH LATIN CAPITAL LETTER R
block|,
literal|"R"
block|,
comment|// Folded result
literal|"Å"
comment|// U+0155: LATIN SMALL LETTER R WITH ACUTE
operator|+
literal|"Å"
comment|// U+0157: LATIN SMALL LETTER R WITH CEDILLA
operator|+
literal|"Å"
comment|// U+0159: LATIN SMALL LETTER R WITH CARON
operator|+
literal|"È"
comment|// U+0211: LATIN SMALL LETTER R WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0213: LATIN SMALL LETTER R WITH INVERTED BREVE
operator|+
literal|"É"
comment|// U+024D: LATIN SMALL LETTER R WITH STROKE
operator|+
literal|"É¼"
comment|// U+027C: LATIN SMALL LETTER R WITH LONG LEG
operator|+
literal|"É½"
comment|// U+027D: LATIN SMALL LETTER R WITH TAIL
operator|+
literal|"É¾"
comment|// U+027E: LATIN SMALL LETTER R WITH FISHHOOK
operator|+
literal|"É¿"
comment|// U+027F: LATIN SMALL LETTER REVERSED R WITH FISHHOOK
operator|+
literal|"áµ£"
comment|// U+1D63: LATIN SUBSCRIPT SMALL LETTER R
operator|+
literal|"áµ²"
comment|// U+1D72: LATIN SMALL LETTER R WITH MIDDLE TILDE
operator|+
literal|"áµ³"
comment|// U+1D73: LATIN SMALL LETTER R WITH FISHHOOK AND MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D89: LATIN SMALL LETTER R WITH PALATAL HOOK
operator|+
literal|"á¹"
comment|// U+1E59: LATIN SMALL LETTER R WITH DOT ABOVE
operator|+
literal|"á¹"
comment|// U+1E5B: LATIN SMALL LETTER R WITH DOT BELOW
operator|+
literal|"á¹"
comment|// U+1E5D: LATIN SMALL LETTER R WITH DOT BELOW AND MACRON
operator|+
literal|"á¹"
comment|// U+1E5F: LATIN SMALL LETTER R WITH LINE BELOW
operator|+
literal|"â¡"
comment|// U+24E1: CIRCLED LATIN SMALL LETTER R
operator|+
literal|"ê"
comment|// U+A75B: LATIN SMALL LETTER R ROTUNDA
operator|+
literal|"ê"
comment|// U+A783: LATIN SMALL LETTER INSULAR R
operator|+
literal|"ï½"
comment|// U+FF52: FULLWIDTH LATIN SMALL LETTER R
block|,
literal|"r"
block|,
comment|// Folded result
literal|"â­"
comment|// U+24AD: PARENTHESIZED LATIN SMALL LETTER R
block|,
literal|"(r)"
block|,
comment|// Folded result
literal|"Å"
comment|// U+015A: LATIN CAPITAL LETTER S WITH ACUTE
operator|+
literal|"Å"
comment|// U+015C: LATIN CAPITAL LETTER S WITH CIRCUMFLEX
operator|+
literal|"Å"
comment|// U+015E: LATIN CAPITAL LETTER S WITH CEDILLA
operator|+
literal|"Å "
comment|// U+0160: LATIN CAPITAL LETTER S WITH CARON
operator|+
literal|"È"
comment|// U+0218: LATIN CAPITAL LETTER S WITH COMMA BELOW
operator|+
literal|"á¹ "
comment|// U+1E60: LATIN CAPITAL LETTER S WITH DOT ABOVE
operator|+
literal|"á¹¢"
comment|// U+1E62: LATIN CAPITAL LETTER S WITH DOT BELOW
operator|+
literal|"á¹¤"
comment|// U+1E64: LATIN CAPITAL LETTER S WITH ACUTE AND DOT ABOVE
operator|+
literal|"á¹¦"
comment|// U+1E66: LATIN CAPITAL LETTER S WITH CARON AND DOT ABOVE
operator|+
literal|"á¹¨"
comment|// U+1E68: LATIN CAPITAL LETTER S WITH DOT BELOW AND DOT ABOVE
operator|+
literal|"â"
comment|// U+24C8: CIRCLED LATIN CAPITAL LETTER S
operator|+
literal|"ê±"
comment|// U+A731: LATIN LETTER SMALL CAPITAL S
operator|+
literal|"ê"
comment|// U+A785: LATIN SMALL LETTER INSULAR S
operator|+
literal|"ï¼³"
comment|// U+FF33: FULLWIDTH LATIN CAPITAL LETTER S
block|,
literal|"S"
block|,
comment|// Folded result
literal|"Å"
comment|// U+015B: LATIN SMALL LETTER S WITH ACUTE
operator|+
literal|"Å"
comment|// U+015D: LATIN SMALL LETTER S WITH CIRCUMFLEX
operator|+
literal|"Å"
comment|// U+015F: LATIN SMALL LETTER S WITH CEDILLA
operator|+
literal|"Å¡"
comment|// U+0161: LATIN SMALL LETTER S WITH CARON
operator|+
literal|"Å¿"
comment|// U+017F: LATIN SMALL LETTER LONG S
operator|+
literal|"È"
comment|// U+0219: LATIN SMALL LETTER S WITH COMMA BELOW
operator|+
literal|"È¿"
comment|// U+023F: LATIN SMALL LETTER S WITH SWASH TAIL
operator|+
literal|"Ê"
comment|// U+0282: LATIN SMALL LETTER S WITH HOOK
operator|+
literal|"áµ´"
comment|// U+1D74: LATIN SMALL LETTER S WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D8A: LATIN SMALL LETTER S WITH PALATAL HOOK
operator|+
literal|"á¹¡"
comment|// U+1E61: LATIN SMALL LETTER S WITH DOT ABOVE
operator|+
literal|"á¹£"
comment|// U+1E63: LATIN SMALL LETTER S WITH DOT BELOW
operator|+
literal|"á¹¥"
comment|// U+1E65: LATIN SMALL LETTER S WITH ACUTE AND DOT ABOVE
operator|+
literal|"á¹§"
comment|// U+1E67: LATIN SMALL LETTER S WITH CARON AND DOT ABOVE
operator|+
literal|"á¹©"
comment|// U+1E69: LATIN SMALL LETTER S WITH DOT BELOW AND DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E9C: LATIN SMALL LETTER LONG S WITH DIAGONAL STROKE
operator|+
literal|"áº"
comment|// U+1E9D: LATIN SMALL LETTER LONG S WITH HIGH STROKE
operator|+
literal|"â¢"
comment|// U+24E2: CIRCLED LATIN SMALL LETTER S
operator|+
literal|"ê"
comment|// U+A784: LATIN CAPITAL LETTER INSULAR S
operator|+
literal|"ï½"
comment|// U+FF53: FULLWIDTH LATIN SMALL LETTER S
block|,
literal|"s"
block|,
comment|// Folded result
literal|"áº"
comment|// U+1E9E: LATIN CAPITAL LETTER SHARP S
block|,
literal|"SS"
block|,
comment|// Folded result
literal|"â®"
comment|// U+24AE: PARENTHESIZED LATIN SMALL LETTER S
block|,
literal|"(s)"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00DF: LATIN SMALL LETTER SHARP S
block|,
literal|"ss"
block|,
comment|// Folded result
literal|"ï¬"
comment|// U+FB06: LATIN SMALL LIGATURE ST
block|,
literal|"st"
block|,
comment|// Folded result
literal|"Å¢"
comment|// U+0162: LATIN CAPITAL LETTER T WITH CEDILLA
operator|+
literal|"Å¤"
comment|// U+0164: LATIN CAPITAL LETTER T WITH CARON
operator|+
literal|"Å¦"
comment|// U+0166: LATIN CAPITAL LETTER T WITH STROKE
operator|+
literal|"Æ¬"
comment|// U+01AC: LATIN CAPITAL LETTER T WITH HOOK
operator|+
literal|"Æ®"
comment|// U+01AE: LATIN CAPITAL LETTER T WITH RETROFLEX HOOK
operator|+
literal|"È"
comment|// U+021A: LATIN CAPITAL LETTER T WITH COMMA BELOW
operator|+
literal|"È¾"
comment|// U+023E: LATIN CAPITAL LETTER T WITH DIAGONAL STROKE
operator|+
literal|"á´"
comment|// U+1D1B: LATIN LETTER SMALL CAPITAL T
operator|+
literal|"á¹ª"
comment|// U+1E6A: LATIN CAPITAL LETTER T WITH DOT ABOVE
operator|+
literal|"á¹¬"
comment|// U+1E6C: LATIN CAPITAL LETTER T WITH DOT BELOW
operator|+
literal|"á¹®"
comment|// U+1E6E: LATIN CAPITAL LETTER T WITH LINE BELOW
operator|+
literal|"á¹°"
comment|// U+1E70: LATIN CAPITAL LETTER T WITH CIRCUMFLEX BELOW
operator|+
literal|"â"
comment|// U+24C9: CIRCLED LATIN CAPITAL LETTER T
operator|+
literal|"ê"
comment|// U+A786: LATIN CAPITAL LETTER INSULAR T
operator|+
literal|"ï¼´"
comment|// U+FF34: FULLWIDTH LATIN CAPITAL LETTER T
block|,
literal|"T"
block|,
comment|// Folded result
literal|"Å£"
comment|// U+0163: LATIN SMALL LETTER T WITH CEDILLA
operator|+
literal|"Å¥"
comment|// U+0165: LATIN SMALL LETTER T WITH CARON
operator|+
literal|"Å§"
comment|// U+0167: LATIN SMALL LETTER T WITH STROKE
operator|+
literal|"Æ«"
comment|// U+01AB: LATIN SMALL LETTER T WITH PALATAL HOOK
operator|+
literal|"Æ­"
comment|// U+01AD: LATIN SMALL LETTER T WITH HOOK
operator|+
literal|"È"
comment|// U+021B: LATIN SMALL LETTER T WITH COMMA BELOW
operator|+
literal|"È¶"
comment|// U+0236: LATIN SMALL LETTER T WITH CURL
operator|+
literal|"Ê"
comment|// U+0287: LATIN SMALL LETTER TURNED T
operator|+
literal|"Ê"
comment|// U+0288: LATIN SMALL LETTER T WITH RETROFLEX HOOK
operator|+
literal|"áµµ"
comment|// U+1D75: LATIN SMALL LETTER T WITH MIDDLE TILDE
operator|+
literal|"á¹«"
comment|// U+1E6B: LATIN SMALL LETTER T WITH DOT ABOVE
operator|+
literal|"á¹­"
comment|// U+1E6D: LATIN SMALL LETTER T WITH DOT BELOW
operator|+
literal|"á¹¯"
comment|// U+1E6F: LATIN SMALL LETTER T WITH LINE BELOW
operator|+
literal|"á¹±"
comment|// U+1E71: LATIN SMALL LETTER T WITH CIRCUMFLEX BELOW
operator|+
literal|"áº"
comment|// U+1E97: LATIN SMALL LETTER T WITH DIAERESIS
operator|+
literal|"â£"
comment|// U+24E3: CIRCLED LATIN SMALL LETTER T
operator|+
literal|"â±¦"
comment|// U+2C66: LATIN SMALL LETTER T WITH DIAGONAL STROKE
operator|+
literal|"ï½"
comment|// U+FF54: FULLWIDTH LATIN SMALL LETTER T
block|,
literal|"t"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00DE: LATIN CAPITAL LETTER THORN
operator|+
literal|"ê¦"
comment|// U+A766: LATIN CAPITAL LETTER THORN WITH STROKE THROUGH DESCENDER
block|,
literal|"TH"
block|,
comment|// Folded result
literal|"ê¨"
comment|// U+A728: LATIN CAPITAL LETTER TZ
block|,
literal|"TZ"
block|,
comment|// Folded result
literal|"â¯"
comment|// U+24AF: PARENTHESIZED LATIN SMALL LETTER T
block|,
literal|"(t)"
block|,
comment|// Folded result
literal|"Ê¨"
comment|// U+02A8: LATIN SMALL LETTER TC DIGRAPH WITH CURL
block|,
literal|"tc"
block|,
comment|// Folded result
literal|"Ã¾"
comment|// U+00FE: LATIN SMALL LETTER THORN
operator|+
literal|"áµº"
comment|// U+1D7A: LATIN SMALL LETTER TH WITH STRIKETHROUGH
operator|+
literal|"ê§"
comment|// U+A767: LATIN SMALL LETTER THORN WITH STROKE THROUGH DESCENDER
block|,
literal|"th"
block|,
comment|// Folded result
literal|"Ê¦"
comment|// U+02A6: LATIN SMALL LETTER TS DIGRAPH
block|,
literal|"ts"
block|,
comment|// Folded result
literal|"ê©"
comment|// U+A729: LATIN SMALL LETTER TZ
block|,
literal|"tz"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00D9: LATIN CAPITAL LETTER U WITH GRAVE
operator|+
literal|"Ã"
comment|// U+00DA: LATIN CAPITAL LETTER U WITH ACUTE
operator|+
literal|"Ã"
comment|// U+00DB: LATIN CAPITAL LETTER U WITH CIRCUMFLEX
operator|+
literal|"Ã"
comment|// U+00DC: LATIN CAPITAL LETTER U WITH DIAERESIS
operator|+
literal|"Å¨"
comment|// U+0168: LATIN CAPITAL LETTER U WITH TILDE
operator|+
literal|"Åª"
comment|// U+016A: LATIN CAPITAL LETTER U WITH MACRON
operator|+
literal|"Å¬"
comment|// U+016C: LATIN CAPITAL LETTER U WITH BREVE
operator|+
literal|"Å®"
comment|// U+016E: LATIN CAPITAL LETTER U WITH RING ABOVE
operator|+
literal|"Å°"
comment|// U+0170: LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
operator|+
literal|"Å²"
comment|// U+0172: LATIN CAPITAL LETTER U WITH OGONEK
operator|+
literal|"Æ¯"
comment|// U+01AF: LATIN CAPITAL LETTER U WITH HORN
operator|+
literal|"Ç"
comment|// U+01D3: LATIN CAPITAL LETTER U WITH CARON
operator|+
literal|"Ç"
comment|// U+01D5: LATIN CAPITAL LETTER U WITH DIAERESIS AND MACRON
operator|+
literal|"Ç"
comment|// U+01D7: LATIN CAPITAL LETTER U WITH DIAERESIS AND ACUTE
operator|+
literal|"Ç"
comment|// U+01D9: LATIN CAPITAL LETTER U WITH DIAERESIS AND CARON
operator|+
literal|"Ç"
comment|// U+01DB: LATIN CAPITAL LETTER U WITH DIAERESIS AND GRAVE
operator|+
literal|"È"
comment|// U+0214: LATIN CAPITAL LETTER U WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0216: LATIN CAPITAL LETTER U WITH INVERTED BREVE
operator|+
literal|"É"
comment|// U+0244: LATIN CAPITAL LETTER U BAR
operator|+
literal|"á´"
comment|// U+1D1C: LATIN LETTER SMALL CAPITAL U
operator|+
literal|"áµ¾"
comment|// U+1D7E: LATIN SMALL CAPITAL LETTER U WITH STROKE
operator|+
literal|"á¹²"
comment|// U+1E72: LATIN CAPITAL LETTER U WITH DIAERESIS BELOW
operator|+
literal|"á¹´"
comment|// U+1E74: LATIN CAPITAL LETTER U WITH TILDE BELOW
operator|+
literal|"á¹¶"
comment|// U+1E76: LATIN CAPITAL LETTER U WITH CIRCUMFLEX BELOW
operator|+
literal|"á¹¸"
comment|// U+1E78: LATIN CAPITAL LETTER U WITH TILDE AND ACUTE
operator|+
literal|"á¹º"
comment|// U+1E7A: LATIN CAPITAL LETTER U WITH MACRON AND DIAERESIS
operator|+
literal|"á»¤"
comment|// U+1EE4: LATIN CAPITAL LETTER U WITH DOT BELOW
operator|+
literal|"á»¦"
comment|// U+1EE6: LATIN CAPITAL LETTER U WITH HOOK ABOVE
operator|+
literal|"á»¨"
comment|// U+1EE8: LATIN CAPITAL LETTER U WITH HORN AND ACUTE
operator|+
literal|"á»ª"
comment|// U+1EEA: LATIN CAPITAL LETTER U WITH HORN AND GRAVE
operator|+
literal|"á»¬"
comment|// U+1EEC: LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE
operator|+
literal|"á»®"
comment|// U+1EEE: LATIN CAPITAL LETTER U WITH HORN AND TILDE
operator|+
literal|"á»°"
comment|// U+1EF0: LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW
operator|+
literal|"â"
comment|// U+24CA: CIRCLED LATIN CAPITAL LETTER U
operator|+
literal|"ï¼µ"
comment|// U+FF35: FULLWIDTH LATIN CAPITAL LETTER U
block|,
literal|"U"
block|,
comment|// Folded result
literal|"Ã¹"
comment|// U+00F9: LATIN SMALL LETTER U WITH GRAVE
operator|+
literal|"Ãº"
comment|// U+00FA: LATIN SMALL LETTER U WITH ACUTE
operator|+
literal|"Ã»"
comment|// U+00FB: LATIN SMALL LETTER U WITH CIRCUMFLEX
operator|+
literal|"Ã¼"
comment|// U+00FC: LATIN SMALL LETTER U WITH DIAERESIS
operator|+
literal|"Å©"
comment|// U+0169: LATIN SMALL LETTER U WITH TILDE
operator|+
literal|"Å«"
comment|// U+016B: LATIN SMALL LETTER U WITH MACRON
operator|+
literal|"Å­"
comment|// U+016D: LATIN SMALL LETTER U WITH BREVE
operator|+
literal|"Å¯"
comment|// U+016F: LATIN SMALL LETTER U WITH RING ABOVE
operator|+
literal|"Å±"
comment|// U+0171: LATIN SMALL LETTER U WITH DOUBLE ACUTE
operator|+
literal|"Å³"
comment|// U+0173: LATIN SMALL LETTER U WITH OGONEK
operator|+
literal|"Æ°"
comment|// U+01B0: LATIN SMALL LETTER U WITH HORN
operator|+
literal|"Ç"
comment|// U+01D4: LATIN SMALL LETTER U WITH CARON
operator|+
literal|"Ç"
comment|// U+01D6: LATIN SMALL LETTER U WITH DIAERESIS AND MACRON
operator|+
literal|"Ç"
comment|// U+01D8: LATIN SMALL LETTER U WITH DIAERESIS AND ACUTE
operator|+
literal|"Ç"
comment|// U+01DA: LATIN SMALL LETTER U WITH DIAERESIS AND CARON
operator|+
literal|"Ç"
comment|// U+01DC: LATIN SMALL LETTER U WITH DIAERESIS AND GRAVE
operator|+
literal|"È"
comment|// U+0215: LATIN SMALL LETTER U WITH DOUBLE GRAVE
operator|+
literal|"È"
comment|// U+0217: LATIN SMALL LETTER U WITH INVERTED BREVE
operator|+
literal|"Ê"
comment|// U+0289: LATIN SMALL LETTER U BAR
operator|+
literal|"áµ¤"
comment|// U+1D64: LATIN SUBSCRIPT SMALL LETTER U
operator|+
literal|"á¶"
comment|// U+1D99: LATIN SMALL LETTER U WITH RETROFLEX HOOK
operator|+
literal|"á¹³"
comment|// U+1E73: LATIN SMALL LETTER U WITH DIAERESIS BELOW
operator|+
literal|"á¹µ"
comment|// U+1E75: LATIN SMALL LETTER U WITH TILDE BELOW
operator|+
literal|"á¹·"
comment|// U+1E77: LATIN SMALL LETTER U WITH CIRCUMFLEX BELOW
operator|+
literal|"á¹¹"
comment|// U+1E79: LATIN SMALL LETTER U WITH TILDE AND ACUTE
operator|+
literal|"á¹»"
comment|// U+1E7B: LATIN SMALL LETTER U WITH MACRON AND DIAERESIS
operator|+
literal|"á»¥"
comment|// U+1EE5: LATIN SMALL LETTER U WITH DOT BELOW
operator|+
literal|"á»§"
comment|// U+1EE7: LATIN SMALL LETTER U WITH HOOK ABOVE
operator|+
literal|"á»©"
comment|// U+1EE9: LATIN SMALL LETTER U WITH HORN AND ACUTE
operator|+
literal|"á»«"
comment|// U+1EEB: LATIN SMALL LETTER U WITH HORN AND GRAVE
operator|+
literal|"á»­"
comment|// U+1EED: LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE
operator|+
literal|"á»¯"
comment|// U+1EEF: LATIN SMALL LETTER U WITH HORN AND TILDE
operator|+
literal|"á»±"
comment|// U+1EF1: LATIN SMALL LETTER U WITH HORN AND DOT BELOW
operator|+
literal|"â¤"
comment|// U+24E4: CIRCLED LATIN SMALL LETTER U
operator|+
literal|"ï½"
comment|// U+FF55: FULLWIDTH LATIN SMALL LETTER U
block|,
literal|"u"
block|,
comment|// Folded result
literal|"â°"
comment|// U+24B0: PARENTHESIZED LATIN SMALL LETTER U
block|,
literal|"(u)"
block|,
comment|// Folded result
literal|"áµ«"
comment|// U+1D6B: LATIN SMALL LETTER UE
block|,
literal|"ue"
block|,
comment|// Folded result
literal|"Æ²"
comment|// U+01B2: LATIN CAPITAL LETTER V WITH HOOK
operator|+
literal|"É"
comment|// U+0245: LATIN CAPITAL LETTER TURNED V
operator|+
literal|"á´ "
comment|// U+1D20: LATIN LETTER SMALL CAPITAL V
operator|+
literal|"á¹¼"
comment|// U+1E7C: LATIN CAPITAL LETTER V WITH TILDE
operator|+
literal|"á¹¾"
comment|// U+1E7E: LATIN CAPITAL LETTER V WITH DOT BELOW
operator|+
literal|"á»¼"
comment|// U+1EFC: LATIN CAPITAL LETTER MIDDLE-WELSH V
operator|+
literal|"â"
comment|// U+24CB: CIRCLED LATIN CAPITAL LETTER V
operator|+
literal|"ê"
comment|// U+A75E: LATIN CAPITAL LETTER V WITH DIAGONAL STROKE
operator|+
literal|"ê¨"
comment|// U+A768: LATIN CAPITAL LETTER VEND
operator|+
literal|"ï¼¶"
comment|// U+FF36: FULLWIDTH LATIN CAPITAL LETTER V
block|,
literal|"V"
block|,
comment|// Folded result
literal|"Ê"
comment|// U+028B: LATIN SMALL LETTER V WITH HOOK
operator|+
literal|"Ê"
comment|// U+028C: LATIN SMALL LETTER TURNED V
operator|+
literal|"áµ¥"
comment|// U+1D65: LATIN SUBSCRIPT SMALL LETTER V
operator|+
literal|"á¶"
comment|// U+1D8C: LATIN SMALL LETTER V WITH PALATAL HOOK
operator|+
literal|"á¹½"
comment|// U+1E7D: LATIN SMALL LETTER V WITH TILDE
operator|+
literal|"á¹¿"
comment|// U+1E7F: LATIN SMALL LETTER V WITH DOT BELOW
operator|+
literal|"â¥"
comment|// U+24E5: CIRCLED LATIN SMALL LETTER V
operator|+
literal|"â±±"
comment|// U+2C71: LATIN SMALL LETTER V WITH RIGHT HOOK
operator|+
literal|"â±´"
comment|// U+2C74: LATIN SMALL LETTER V WITH CURL
operator|+
literal|"ê"
comment|// U+A75F: LATIN SMALL LETTER V WITH DIAGONAL STROKE
operator|+
literal|"ï½"
comment|// U+FF56: FULLWIDTH LATIN SMALL LETTER V
block|,
literal|"v"
block|,
comment|// Folded result
literal|"ê "
comment|// U+A760: LATIN CAPITAL LETTER VY
block|,
literal|"VY"
block|,
comment|// Folded result
literal|"â±"
comment|// U+24B1: PARENTHESIZED LATIN SMALL LETTER V
block|,
literal|"(v)"
block|,
comment|// Folded result
literal|"ê¡"
comment|// U+A761: LATIN SMALL LETTER VY
block|,
literal|"vy"
block|,
comment|// Folded result
literal|"Å´"
comment|// U+0174: LATIN CAPITAL LETTER W WITH CIRCUMFLEX
operator|+
literal|"Ç·"
comment|// U+01F7: LATIN CAPITAL LETTER WYNN
operator|+
literal|"á´¡"
comment|// U+1D21: LATIN LETTER SMALL CAPITAL W
operator|+
literal|"áº"
comment|// U+1E80: LATIN CAPITAL LETTER W WITH GRAVE
operator|+
literal|"áº"
comment|// U+1E82: LATIN CAPITAL LETTER W WITH ACUTE
operator|+
literal|"áº"
comment|// U+1E84: LATIN CAPITAL LETTER W WITH DIAERESIS
operator|+
literal|"áº"
comment|// U+1E86: LATIN CAPITAL LETTER W WITH DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E88: LATIN CAPITAL LETTER W WITH DOT BELOW
operator|+
literal|"â"
comment|// U+24CC: CIRCLED LATIN CAPITAL LETTER W
operator|+
literal|"â±²"
comment|// U+2C72: LATIN CAPITAL LETTER W WITH HOOK
operator|+
literal|"ï¼·"
comment|// U+FF37: FULLWIDTH LATIN CAPITAL LETTER W
block|,
literal|"W"
block|,
comment|// Folded result
literal|"Åµ"
comment|// U+0175: LATIN SMALL LETTER W WITH CIRCUMFLEX
operator|+
literal|"Æ¿"
comment|// U+01BF: LATIN LETTER WYNN
operator|+
literal|"Ê"
comment|// U+028D: LATIN SMALL LETTER TURNED W
operator|+
literal|"áº"
comment|// U+1E81: LATIN SMALL LETTER W WITH GRAVE
operator|+
literal|"áº"
comment|// U+1E83: LATIN SMALL LETTER W WITH ACUTE
operator|+
literal|"áº"
comment|// U+1E85: LATIN SMALL LETTER W WITH DIAERESIS
operator|+
literal|"áº"
comment|// U+1E87: LATIN SMALL LETTER W WITH DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E89: LATIN SMALL LETTER W WITH DOT BELOW
operator|+
literal|"áº"
comment|// U+1E98: LATIN SMALL LETTER W WITH RING ABOVE
operator|+
literal|"â¦"
comment|// U+24E6: CIRCLED LATIN SMALL LETTER W
operator|+
literal|"â±³"
comment|// U+2C73: LATIN SMALL LETTER W WITH HOOK
operator|+
literal|"ï½"
comment|// U+FF57: FULLWIDTH LATIN SMALL LETTER W
block|,
literal|"w"
block|,
comment|// Folded result
literal|"â²"
comment|// U+24B2: PARENTHESIZED LATIN SMALL LETTER W
block|,
literal|"(w)"
block|,
comment|// Folded result
literal|"áº"
comment|// U+1E8A: LATIN CAPITAL LETTER X WITH DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E8C: LATIN CAPITAL LETTER X WITH DIAERESIS
operator|+
literal|"â"
comment|// U+24CD: CIRCLED LATIN CAPITAL LETTER X
operator|+
literal|"ï¼¸"
comment|// U+FF38: FULLWIDTH LATIN CAPITAL LETTER X
block|,
literal|"X"
block|,
comment|// Folded result
literal|"á¶"
comment|// U+1D8D: LATIN SMALL LETTER X WITH PALATAL HOOK
operator|+
literal|"áº"
comment|// U+1E8B: LATIN SMALL LETTER X WITH DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E8D: LATIN SMALL LETTER X WITH DIAERESIS
operator|+
literal|"â"
comment|// U+2093: LATIN SUBSCRIPT SMALL LETTER X
operator|+
literal|"â§"
comment|// U+24E7: CIRCLED LATIN SMALL LETTER X
operator|+
literal|"ï½"
comment|// U+FF58: FULLWIDTH LATIN SMALL LETTER X
block|,
literal|"x"
block|,
comment|// Folded result
literal|"â³"
comment|// U+24B3: PARENTHESIZED LATIN SMALL LETTER X
block|,
literal|"(x)"
block|,
comment|// Folded result
literal|"Ã"
comment|// U+00DD: LATIN CAPITAL LETTER Y WITH ACUTE
operator|+
literal|"Å¶"
comment|// U+0176: LATIN CAPITAL LETTER Y WITH CIRCUMFLEX
operator|+
literal|"Å¸"
comment|// U+0178: LATIN CAPITAL LETTER Y WITH DIAERESIS
operator|+
literal|"Æ³"
comment|// U+01B3: LATIN CAPITAL LETTER Y WITH HOOK
operator|+
literal|"È²"
comment|// U+0232: LATIN CAPITAL LETTER Y WITH MACRON
operator|+
literal|"É"
comment|// U+024E: LATIN CAPITAL LETTER Y WITH STROKE
operator|+
literal|"Ê"
comment|// U+028F: LATIN LETTER SMALL CAPITAL Y
operator|+
literal|"áº"
comment|// U+1E8E: LATIN CAPITAL LETTER Y WITH DOT ABOVE
operator|+
literal|"á»²"
comment|// U+1EF2: LATIN CAPITAL LETTER Y WITH GRAVE
operator|+
literal|"á»´"
comment|// U+1EF4: LATIN CAPITAL LETTER Y WITH DOT BELOW
operator|+
literal|"á»¶"
comment|// U+1EF6: LATIN CAPITAL LETTER Y WITH HOOK ABOVE
operator|+
literal|"á»¸"
comment|// U+1EF8: LATIN CAPITAL LETTER Y WITH TILDE
operator|+
literal|"á»¾"
comment|// U+1EFE: LATIN CAPITAL LETTER Y WITH LOOP
operator|+
literal|"â"
comment|// U+24CE: CIRCLED LATIN CAPITAL LETTER Y
operator|+
literal|"ï¼¹"
comment|// U+FF39: FULLWIDTH LATIN CAPITAL LETTER Y
block|,
literal|"Y"
block|,
comment|// Folded result
literal|"Ã½"
comment|// U+00FD: LATIN SMALL LETTER Y WITH ACUTE
operator|+
literal|"Ã¿"
comment|// U+00FF: LATIN SMALL LETTER Y WITH DIAERESIS
operator|+
literal|"Å·"
comment|// U+0177: LATIN SMALL LETTER Y WITH CIRCUMFLEX
operator|+
literal|"Æ´"
comment|// U+01B4: LATIN SMALL LETTER Y WITH HOOK
operator|+
literal|"È³"
comment|// U+0233: LATIN SMALL LETTER Y WITH MACRON
operator|+
literal|"É"
comment|// U+024F: LATIN SMALL LETTER Y WITH STROKE
operator|+
literal|"Ê"
comment|// U+028E: LATIN SMALL LETTER TURNED Y
operator|+
literal|"áº"
comment|// U+1E8F: LATIN SMALL LETTER Y WITH DOT ABOVE
operator|+
literal|"áº"
comment|// U+1E99: LATIN SMALL LETTER Y WITH RING ABOVE
operator|+
literal|"á»³"
comment|// U+1EF3: LATIN SMALL LETTER Y WITH GRAVE
operator|+
literal|"á»µ"
comment|// U+1EF5: LATIN SMALL LETTER Y WITH DOT BELOW
operator|+
literal|"á»·"
comment|// U+1EF7: LATIN SMALL LETTER Y WITH HOOK ABOVE
operator|+
literal|"á»¹"
comment|// U+1EF9: LATIN SMALL LETTER Y WITH TILDE
operator|+
literal|"á»¿"
comment|// U+1EFF: LATIN SMALL LETTER Y WITH LOOP
operator|+
literal|"â¨"
comment|// U+24E8: CIRCLED LATIN SMALL LETTER Y
operator|+
literal|"ï½"
comment|// U+FF59: FULLWIDTH LATIN SMALL LETTER Y
block|,
literal|"y"
block|,
comment|// Folded result
literal|"â´"
comment|// U+24B4: PARENTHESIZED LATIN SMALL LETTER Y
block|,
literal|"(y)"
block|,
comment|// Folded result
literal|"Å¹"
comment|// U+0179: LATIN CAPITAL LETTER Z WITH ACUTE
operator|+
literal|"Å»"
comment|// U+017B: LATIN CAPITAL LETTER Z WITH DOT ABOVE
operator|+
literal|"Å½"
comment|// U+017D: LATIN CAPITAL LETTER Z WITH CARON
operator|+
literal|"Æµ"
comment|// U+01B5: LATIN CAPITAL LETTER Z WITH STROKE
operator|+
literal|"È"
comment|// U+021C: LATIN CAPITAL LETTER YOGH
operator|+
literal|"È¤"
comment|// U+0224: LATIN CAPITAL LETTER Z WITH HOOK
operator|+
literal|"á´¢"
comment|// U+1D22: LATIN LETTER SMALL CAPITAL Z
operator|+
literal|"áº"
comment|// U+1E90: LATIN CAPITAL LETTER Z WITH CIRCUMFLEX
operator|+
literal|"áº"
comment|// U+1E92: LATIN CAPITAL LETTER Z WITH DOT BELOW
operator|+
literal|"áº"
comment|// U+1E94: LATIN CAPITAL LETTER Z WITH LINE BELOW
operator|+
literal|"â"
comment|// U+24CF: CIRCLED LATIN CAPITAL LETTER Z
operator|+
literal|"â±«"
comment|// U+2C6B: LATIN CAPITAL LETTER Z WITH DESCENDER
operator|+
literal|"ê¢"
comment|// U+A762: LATIN CAPITAL LETTER VISIGOTHIC Z
operator|+
literal|"ï¼º"
comment|// U+FF3A: FULLWIDTH LATIN CAPITAL LETTER Z
block|,
literal|"Z"
block|,
comment|// Folded result
literal|"Åº"
comment|// U+017A: LATIN SMALL LETTER Z WITH ACUTE
operator|+
literal|"Å¼"
comment|// U+017C: LATIN SMALL LETTER Z WITH DOT ABOVE
operator|+
literal|"Å¾"
comment|// U+017E: LATIN SMALL LETTER Z WITH CARON
operator|+
literal|"Æ¶"
comment|// U+01B6: LATIN SMALL LETTER Z WITH STROKE
operator|+
literal|"È"
comment|// U+021D: LATIN SMALL LETTER YOGH
operator|+
literal|"È¥"
comment|// U+0225: LATIN SMALL LETTER Z WITH HOOK
operator|+
literal|"É"
comment|// U+0240: LATIN SMALL LETTER Z WITH SWASH TAIL
operator|+
literal|"Ê"
comment|// U+0290: LATIN SMALL LETTER Z WITH RETROFLEX HOOK
operator|+
literal|"Ê"
comment|// U+0291: LATIN SMALL LETTER Z WITH CURL
operator|+
literal|"áµ¶"
comment|// U+1D76: LATIN SMALL LETTER Z WITH MIDDLE TILDE
operator|+
literal|"á¶"
comment|// U+1D8E: LATIN SMALL LETTER Z WITH PALATAL HOOK
operator|+
literal|"áº"
comment|// U+1E91: LATIN SMALL LETTER Z WITH CIRCUMFLEX
operator|+
literal|"áº"
comment|// U+1E93: LATIN SMALL LETTER Z WITH DOT BELOW
operator|+
literal|"áº"
comment|// U+1E95: LATIN SMALL LETTER Z WITH LINE BELOW
operator|+
literal|"â©"
comment|// U+24E9: CIRCLED LATIN SMALL LETTER Z
operator|+
literal|"â±¬"
comment|// U+2C6C: LATIN SMALL LETTER Z WITH DESCENDER
operator|+
literal|"ê£"
comment|// U+A763: LATIN SMALL LETTER VISIGOTHIC Z
operator|+
literal|"ï½"
comment|// U+FF5A: FULLWIDTH LATIN SMALL LETTER Z
block|,
literal|"z"
block|,
comment|// Folded result
literal|"âµ"
comment|// U+24B5: PARENTHESIZED LATIN SMALL LETTER Z
block|,
literal|"(z)"
block|,
comment|// Folded result
literal|"â°"
comment|// U+2070: SUPERSCRIPT ZERO
operator|+
literal|"â"
comment|// U+2080: SUBSCRIPT ZERO
operator|+
literal|"âª"
comment|// U+24EA: CIRCLED DIGIT ZERO
operator|+
literal|"â¿"
comment|// U+24FF: NEGATIVE CIRCLED DIGIT ZERO
operator|+
literal|"ï¼"
comment|// U+FF10: FULLWIDTH DIGIT ZERO
block|,
literal|"0"
block|,
comment|// Folded result
literal|"Â¹"
comment|// U+00B9: SUPERSCRIPT ONE
operator|+
literal|"â"
comment|// U+2081: SUBSCRIPT ONE
operator|+
literal|"â "
comment|// U+2460: CIRCLED DIGIT ONE
operator|+
literal|"âµ"
comment|// U+24F5: DOUBLE CIRCLED DIGIT ONE
operator|+
literal|"â¶"
comment|// U+2776: DINGBAT NEGATIVE CIRCLED DIGIT ONE
operator|+
literal|"â"
comment|// U+2780: DINGBAT CIRCLED SANS-SERIF DIGIT ONE
operator|+
literal|"â"
comment|// U+278A: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE
operator|+
literal|"ï¼"
comment|// U+FF11: FULLWIDTH DIGIT ONE
block|,
literal|"1"
block|,
comment|// Folded result
literal|"â"
comment|// U+2488: DIGIT ONE FULL STOP
block|,
literal|"1."
block|,
comment|// Folded result
literal|"â´"
comment|// U+2474: PARENTHESIZED DIGIT ONE
block|,
literal|"(1)"
block|,
comment|// Folded result
literal|"Â²"
comment|// U+00B2: SUPERSCRIPT TWO
operator|+
literal|"â"
comment|// U+2082: SUBSCRIPT TWO
operator|+
literal|"â¡"
comment|// U+2461: CIRCLED DIGIT TWO
operator|+
literal|"â¶"
comment|// U+24F6: DOUBLE CIRCLED DIGIT TWO
operator|+
literal|"â·"
comment|// U+2777: DINGBAT NEGATIVE CIRCLED DIGIT TWO
operator|+
literal|"â"
comment|// U+2781: DINGBAT CIRCLED SANS-SERIF DIGIT TWO
operator|+
literal|"â"
comment|// U+278B: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO
operator|+
literal|"ï¼"
comment|// U+FF12: FULLWIDTH DIGIT TWO
block|,
literal|"2"
block|,
comment|// Folded result
literal|"â"
comment|// U+2489: DIGIT TWO FULL STOP
block|,
literal|"2."
block|,
comment|// Folded result
literal|"âµ"
comment|// U+2475: PARENTHESIZED DIGIT TWO
block|,
literal|"(2)"
block|,
comment|// Folded result
literal|"Â³"
comment|// U+00B3: SUPERSCRIPT THREE
operator|+
literal|"â"
comment|// U+2083: SUBSCRIPT THREE
operator|+
literal|"â¢"
comment|// U+2462: CIRCLED DIGIT THREE
operator|+
literal|"â·"
comment|// U+24F7: DOUBLE CIRCLED DIGIT THREE
operator|+
literal|"â¸"
comment|// U+2778: DINGBAT NEGATIVE CIRCLED DIGIT THREE
operator|+
literal|"â"
comment|// U+2782: DINGBAT CIRCLED SANS-SERIF DIGIT THREE
operator|+
literal|"â"
comment|// U+278C: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE
operator|+
literal|"ï¼"
comment|// U+FF13: FULLWIDTH DIGIT THREE
block|,
literal|"3"
block|,
comment|// Folded result
literal|"â"
comment|// U+248A: DIGIT THREE FULL STOP
block|,
literal|"3."
block|,
comment|// Folded result
literal|"â¶"
comment|// U+2476: PARENTHESIZED DIGIT THREE
block|,
literal|"(3)"
block|,
comment|// Folded result
literal|"â´"
comment|// U+2074: SUPERSCRIPT FOUR
operator|+
literal|"â"
comment|// U+2084: SUBSCRIPT FOUR
operator|+
literal|"â£"
comment|// U+2463: CIRCLED DIGIT FOUR
operator|+
literal|"â¸"
comment|// U+24F8: DOUBLE CIRCLED DIGIT FOUR
operator|+
literal|"â¹"
comment|// U+2779: DINGBAT NEGATIVE CIRCLED DIGIT FOUR
operator|+
literal|"â"
comment|// U+2783: DINGBAT CIRCLED SANS-SERIF DIGIT FOUR
operator|+
literal|"â"
comment|// U+278D: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR
operator|+
literal|"ï¼"
comment|// U+FF14: FULLWIDTH DIGIT FOUR
block|,
literal|"4"
block|,
comment|// Folded result
literal|"â"
comment|// U+248B: DIGIT FOUR FULL STOP
block|,
literal|"4."
block|,
comment|// Folded result
literal|"â·"
comment|// U+2477: PARENTHESIZED DIGIT FOUR
block|,
literal|"(4)"
block|,
comment|// Folded result
literal|"âµ"
comment|// U+2075: SUPERSCRIPT FIVE
operator|+
literal|"â"
comment|// U+2085: SUBSCRIPT FIVE
operator|+
literal|"â¤"
comment|// U+2464: CIRCLED DIGIT FIVE
operator|+
literal|"â¹"
comment|// U+24F9: DOUBLE CIRCLED DIGIT FIVE
operator|+
literal|"âº"
comment|// U+277A: DINGBAT NEGATIVE CIRCLED DIGIT FIVE
operator|+
literal|"â"
comment|// U+2784: DINGBAT CIRCLED SANS-SERIF DIGIT FIVE
operator|+
literal|"â"
comment|// U+278E: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE
operator|+
literal|"ï¼"
comment|// U+FF15: FULLWIDTH DIGIT FIVE
block|,
literal|"5"
block|,
comment|// Folded result
literal|"â"
comment|// U+248C: DIGIT FIVE FULL STOP
block|,
literal|"5."
block|,
comment|// Folded result
literal|"â¸"
comment|// U+2478: PARENTHESIZED DIGIT FIVE
block|,
literal|"(5)"
block|,
comment|// Folded result
literal|"â¶"
comment|// U+2076: SUPERSCRIPT SIX
operator|+
literal|"â"
comment|// U+2086: SUBSCRIPT SIX
operator|+
literal|"â¥"
comment|// U+2465: CIRCLED DIGIT SIX
operator|+
literal|"âº"
comment|// U+24FA: DOUBLE CIRCLED DIGIT SIX
operator|+
literal|"â»"
comment|// U+277B: DINGBAT NEGATIVE CIRCLED DIGIT SIX
operator|+
literal|"â"
comment|// U+2785: DINGBAT CIRCLED SANS-SERIF DIGIT SIX
operator|+
literal|"â"
comment|// U+278F: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX
operator|+
literal|"ï¼"
comment|// U+FF16: FULLWIDTH DIGIT SIX
block|,
literal|"6"
block|,
comment|// Folded result
literal|"â"
comment|// U+248D: DIGIT SIX FULL STOP
block|,
literal|"6."
block|,
comment|// Folded result
literal|"â¹"
comment|// U+2479: PARENTHESIZED DIGIT SIX
block|,
literal|"(6)"
block|,
comment|// Folded result
literal|"â·"
comment|// U+2077: SUPERSCRIPT SEVEN
operator|+
literal|"â"
comment|// U+2087: SUBSCRIPT SEVEN
operator|+
literal|"â¦"
comment|// U+2466: CIRCLED DIGIT SEVEN
operator|+
literal|"â»"
comment|// U+24FB: DOUBLE CIRCLED DIGIT SEVEN
operator|+
literal|"â¼"
comment|// U+277C: DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
operator|+
literal|"â"
comment|// U+2786: DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN
operator|+
literal|"â"
comment|// U+2790: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN
operator|+
literal|"ï¼"
comment|// U+FF17: FULLWIDTH DIGIT SEVEN
block|,
literal|"7"
block|,
comment|// Folded result
literal|"â"
comment|// U+248E: DIGIT SEVEN FULL STOP
block|,
literal|"7."
block|,
comment|// Folded result
literal|"âº"
comment|// U+247A: PARENTHESIZED DIGIT SEVEN
block|,
literal|"(7)"
block|,
comment|// Folded result
literal|"â¸"
comment|// U+2078: SUPERSCRIPT EIGHT
operator|+
literal|"â"
comment|// U+2088: SUBSCRIPT EIGHT
operator|+
literal|"â§"
comment|// U+2467: CIRCLED DIGIT EIGHT
operator|+
literal|"â¼"
comment|// U+24FC: DOUBLE CIRCLED DIGIT EIGHT
operator|+
literal|"â½"
comment|// U+277D: DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
operator|+
literal|"â"
comment|// U+2787: DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT
operator|+
literal|"â"
comment|// U+2791: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT
operator|+
literal|"ï¼"
comment|// U+FF18: FULLWIDTH DIGIT EIGHT
block|,
literal|"8"
block|,
comment|// Folded result
literal|"â"
comment|// U+248F: DIGIT EIGHT FULL STOP
block|,
literal|"8."
block|,
comment|// Folded result
literal|"â»"
comment|// U+247B: PARENTHESIZED DIGIT EIGHT
block|,
literal|"(8)"
block|,
comment|// Folded result
literal|"â¹"
comment|// U+2079: SUPERSCRIPT NINE
operator|+
literal|"â"
comment|// U+2089: SUBSCRIPT NINE
operator|+
literal|"â¨"
comment|// U+2468: CIRCLED DIGIT NINE
operator|+
literal|"â½"
comment|// U+24FD: DOUBLE CIRCLED DIGIT NINE
operator|+
literal|"â¾"
comment|// U+277E: DINGBAT NEGATIVE CIRCLED DIGIT NINE
operator|+
literal|"â"
comment|// U+2788: DINGBAT CIRCLED SANS-SERIF DIGIT NINE
operator|+
literal|"â"
comment|// U+2792: DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE
operator|+
literal|"ï¼"
comment|// U+FF19: FULLWIDTH DIGIT NINE
block|,
literal|"9"
block|,
comment|// Folded result
literal|"â"
comment|// U+2490: DIGIT NINE FULL STOP
block|,
literal|"9."
block|,
comment|// Folded result
literal|"â¼"
comment|// U+247C: PARENTHESIZED DIGIT NINE
block|,
literal|"(9)"
block|,
comment|// Folded result
literal|"â©"
comment|// U+2469: CIRCLED NUMBER TEN
operator|+
literal|"â¾"
comment|// U+24FE: DOUBLE CIRCLED NUMBER TEN
operator|+
literal|"â¿"
comment|// U+277F: DINGBAT NEGATIVE CIRCLED NUMBER TEN
operator|+
literal|"â"
comment|// U+2789: DINGBAT CIRCLED SANS-SERIF NUMBER TEN
operator|+
literal|"â"
comment|// U+2793: DINGBAT NEGATIVE CIRCLED SANS-SERIF NUMBER TEN
block|,
literal|"10"
block|,
comment|// Folded result
literal|"â"
comment|// U+2491: NUMBER TEN FULL STOP
block|,
literal|"10."
block|,
comment|// Folded result
literal|"â½"
comment|// U+247D: PARENTHESIZED NUMBER TEN
block|,
literal|"(10)"
block|,
comment|// Folded result
literal|"âª"
comment|// U+246A: CIRCLED NUMBER ELEVEN
operator|+
literal|"â«"
comment|// U+24EB: NEGATIVE CIRCLED NUMBER ELEVEN
block|,
literal|"11"
block|,
comment|// Folded result
literal|"â"
comment|// U+2492: NUMBER ELEVEN FULL STOP
block|,
literal|"11."
block|,
comment|// Folded result
literal|"â¾"
comment|// U+247E: PARENTHESIZED NUMBER ELEVEN
block|,
literal|"(11)"
block|,
comment|// Folded result
literal|"â«"
comment|// U+246B: CIRCLED NUMBER TWELVE
operator|+
literal|"â¬"
comment|// U+24EC: NEGATIVE CIRCLED NUMBER TWELVE
block|,
literal|"12"
block|,
comment|// Folded result
literal|"â"
comment|// U+2493: NUMBER TWELVE FULL STOP
block|,
literal|"12."
block|,
comment|// Folded result
literal|"â¿"
comment|// U+247F: PARENTHESIZED NUMBER TWELVE
block|,
literal|"(12)"
block|,
comment|// Folded result
literal|"â¬"
comment|// U+246C: CIRCLED NUMBER THIRTEEN
operator|+
literal|"â­"
comment|// U+24ED: NEGATIVE CIRCLED NUMBER THIRTEEN
block|,
literal|"13"
block|,
comment|// Folded result
literal|"â"
comment|// U+2494: NUMBER THIRTEEN FULL STOP
block|,
literal|"13."
block|,
comment|// Folded result
literal|"â"
comment|// U+2480: PARENTHESIZED NUMBER THIRTEEN
block|,
literal|"(13)"
block|,
comment|// Folded result
literal|"â­"
comment|// U+246D: CIRCLED NUMBER FOURTEEN
operator|+
literal|"â®"
comment|// U+24EE: NEGATIVE CIRCLED NUMBER FOURTEEN
block|,
literal|"14"
block|,
comment|// Folded result
literal|"â"
comment|// U+2495: NUMBER FOURTEEN FULL STOP
block|,
literal|"14."
block|,
comment|// Folded result
literal|"â"
comment|// U+2481: PARENTHESIZED NUMBER FOURTEEN
block|,
literal|"(14)"
block|,
comment|// Folded result
literal|"â®"
comment|// U+246E: CIRCLED NUMBER FIFTEEN
operator|+
literal|"â¯"
comment|// U+24EF: NEGATIVE CIRCLED NUMBER FIFTEEN
block|,
literal|"15"
block|,
comment|// Folded result
literal|"â"
comment|// U+2496: NUMBER FIFTEEN FULL STOP
block|,
literal|"15."
block|,
comment|// Folded result
literal|"â"
comment|// U+2482: PARENTHESIZED NUMBER FIFTEEN
block|,
literal|"(15)"
block|,
comment|// Folded result
literal|"â¯"
comment|// U+246F: CIRCLED NUMBER SIXTEEN
operator|+
literal|"â°"
comment|// U+24F0: NEGATIVE CIRCLED NUMBER SIXTEEN
block|,
literal|"16"
block|,
comment|// Folded result
literal|"â"
comment|// U+2497: NUMBER SIXTEEN FULL STOP
block|,
literal|"16."
block|,
comment|// Folded result
literal|"â"
comment|// U+2483: PARENTHESIZED NUMBER SIXTEEN
block|,
literal|"(16)"
block|,
comment|// Folded result
literal|"â°"
comment|// U+2470: CIRCLED NUMBER SEVENTEEN
operator|+
literal|"â±"
comment|// U+24F1: NEGATIVE CIRCLED NUMBER SEVENTEEN
block|,
literal|"17"
block|,
comment|// Folded result
literal|"â"
comment|// U+2498: NUMBER SEVENTEEN FULL STOP
block|,
literal|"17."
block|,
comment|// Folded result
literal|"â"
comment|// U+2484: PARENTHESIZED NUMBER SEVENTEEN
block|,
literal|"(17)"
block|,
comment|// Folded result
literal|"â±"
comment|// U+2471: CIRCLED NUMBER EIGHTEEN
operator|+
literal|"â²"
comment|// U+24F2: NEGATIVE CIRCLED NUMBER EIGHTEEN
block|,
literal|"18"
block|,
comment|// Folded result
literal|"â"
comment|// U+2499: NUMBER EIGHTEEN FULL STOP
block|,
literal|"18."
block|,
comment|// Folded result
literal|"â"
comment|// U+2485: PARENTHESIZED NUMBER EIGHTEEN
block|,
literal|"(18)"
block|,
comment|// Folded result
literal|"â²"
comment|// U+2472: CIRCLED NUMBER NINETEEN
operator|+
literal|"â³"
comment|// U+24F3: NEGATIVE CIRCLED NUMBER NINETEEN
block|,
literal|"19"
block|,
comment|// Folded result
literal|"â"
comment|// U+249A: NUMBER NINETEEN FULL STOP
block|,
literal|"19."
block|,
comment|// Folded result
literal|"â"
comment|// U+2486: PARENTHESIZED NUMBER NINETEEN
block|,
literal|"(19)"
block|,
comment|// Folded result
literal|"â³"
comment|// U+2473: CIRCLED NUMBER TWENTY
operator|+
literal|"â´"
comment|// U+24F4: NEGATIVE CIRCLED NUMBER TWENTY
block|,
literal|"20"
block|,
comment|// Folded result
literal|"â"
comment|// U+249B: NUMBER TWENTY FULL STOP
block|,
literal|"20."
block|,
comment|// Folded result
literal|"â"
comment|// U+2487: PARENTHESIZED NUMBER TWENTY
block|,
literal|"(20)"
block|,
comment|// Folded result
literal|"Â«"
comment|// U+00AB: LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
operator|+
literal|"Â»"
comment|// U+00BB: RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
operator|+
literal|"â"
comment|// U+201C: LEFT DOUBLE QUOTATION MARK
operator|+
literal|"â"
comment|// U+201D: RIGHT DOUBLE QUOTATION MARK
operator|+
literal|"â"
comment|// U+201E: DOUBLE LOW-9 QUOTATION MARK
operator|+
literal|"â³"
comment|// U+2033: DOUBLE PRIME
operator|+
literal|"â¶"
comment|// U+2036: REVERSED DOUBLE PRIME
operator|+
literal|"â"
comment|// U+275D: HEAVY DOUBLE TURNED COMMA QUOTATION MARK ORNAMENT
operator|+
literal|"â"
comment|// U+275E: HEAVY DOUBLE COMMA QUOTATION MARK ORNAMENT
operator|+
literal|"â®"
comment|// U+276E: HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT
operator|+
literal|"â¯"
comment|// U+276F: HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT
operator|+
literal|"ï¼"
comment|// U+FF02: FULLWIDTH QUOTATION MARK
block|,
literal|"\""
block|,
comment|// Folded result
literal|"â"
comment|// U+2018: LEFT SINGLE QUOTATION MARK
operator|+
literal|"â"
comment|// U+2019: RIGHT SINGLE QUOTATION MARK
operator|+
literal|"â"
comment|// U+201A: SINGLE LOW-9 QUOTATION MARK
operator|+
literal|"â"
comment|// U+201B: SINGLE HIGH-REVERSED-9 QUOTATION MARK
operator|+
literal|"â²"
comment|// U+2032: PRIME
operator|+
literal|"âµ"
comment|// U+2035: REVERSED PRIME
operator|+
literal|"â¹"
comment|// U+2039: SINGLE LEFT-POINTING ANGLE QUOTATION MARK
operator|+
literal|"âº"
comment|// U+203A: SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
operator|+
literal|"â"
comment|// U+275B: HEAVY SINGLE TURNED COMMA QUOTATION MARK ORNAMENT
operator|+
literal|"â"
comment|// U+275C: HEAVY SINGLE COMMA QUOTATION MARK ORNAMENT
operator|+
literal|"ï¼"
comment|// U+FF07: FULLWIDTH APOSTROPHE
block|,
literal|"'"
block|,
comment|// Folded result
literal|"â"
comment|// U+2010: HYPHEN
operator|+
literal|"â"
comment|// U+2011: NON-BREAKING HYPHEN
operator|+
literal|"â"
comment|// U+2012: FIGURE DASH
operator|+
literal|"â"
comment|// U+2013: EN DASH
operator|+
literal|"â"
comment|// U+2014: EM DASH
operator|+
literal|"â»"
comment|// U+207B: SUPERSCRIPT MINUS
operator|+
literal|"â"
comment|// U+208B: SUBSCRIPT MINUS
operator|+
literal|"ï¼"
comment|// U+FF0D: FULLWIDTH HYPHEN-MINUS
block|,
literal|"-"
block|,
comment|// Folded result
literal|"â"
comment|// U+2045: LEFT SQUARE BRACKET WITH QUILL
operator|+
literal|"â²"
comment|// U+2772: LIGHT LEFT TORTOISE SHELL BRACKET ORNAMENT
operator|+
literal|"ï¼»"
comment|// U+FF3B: FULLWIDTH LEFT SQUARE BRACKET
block|,
literal|"["
block|,
comment|// Folded result
literal|"â"
comment|// U+2046: RIGHT SQUARE BRACKET WITH QUILL
operator|+
literal|"â³"
comment|// U+2773: LIGHT RIGHT TORTOISE SHELL BRACKET ORNAMENT
operator|+
literal|"ï¼½"
comment|// U+FF3D: FULLWIDTH RIGHT SQUARE BRACKET
block|,
literal|"]"
block|,
comment|// Folded result
literal|"â½"
comment|// U+207D: SUPERSCRIPT LEFT PARENTHESIS
operator|+
literal|"â"
comment|// U+208D: SUBSCRIPT LEFT PARENTHESIS
operator|+
literal|"â¨"
comment|// U+2768: MEDIUM LEFT PARENTHESIS ORNAMENT
operator|+
literal|"âª"
comment|// U+276A: MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT
operator|+
literal|"ï¼"
comment|// U+FF08: FULLWIDTH LEFT PARENTHESIS
block|,
literal|"("
block|,
comment|// Folded result
literal|"â¸¨"
comment|// U+2E28: LEFT DOUBLE PARENTHESIS
block|,
literal|"(("
block|,
comment|// Folded result
literal|"â¾"
comment|// U+207E: SUPERSCRIPT RIGHT PARENTHESIS
operator|+
literal|"â"
comment|// U+208E: SUBSCRIPT RIGHT PARENTHESIS
operator|+
literal|"â©"
comment|// U+2769: MEDIUM RIGHT PARENTHESIS ORNAMENT
operator|+
literal|"â«"
comment|// U+276B: MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT
operator|+
literal|"ï¼"
comment|// U+FF09: FULLWIDTH RIGHT PARENTHESIS
block|,
literal|")"
block|,
comment|// Folded result
literal|"â¸©"
comment|// U+2E29: RIGHT DOUBLE PARENTHESIS
block|,
literal|"))"
block|,
comment|// Folded result
literal|"â¬"
comment|// U+276C: MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT
operator|+
literal|"â°"
comment|// U+2770: HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT
operator|+
literal|"ï¼"
comment|// U+FF1C: FULLWIDTH LESS-THAN SIGN
block|,
literal|"<"
block|,
comment|// Folded result
literal|"â­"
comment|// U+276D: MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT
operator|+
literal|"â±"
comment|// U+2771: HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT
operator|+
literal|"ï¼"
comment|// U+FF1E: FULLWIDTH GREATER-THAN SIGN
block|,
literal|">"
block|,
comment|// Folded result
literal|"â´"
comment|// U+2774: MEDIUM LEFT CURLY BRACKET ORNAMENT
operator|+
literal|"ï½"
comment|// U+FF5B: FULLWIDTH LEFT CURLY BRACKET
block|,
literal|"{"
block|,
comment|// Folded result
literal|"âµ"
comment|// U+2775: MEDIUM RIGHT CURLY BRACKET ORNAMENT
operator|+
literal|"ï½"
comment|// U+FF5D: FULLWIDTH RIGHT CURLY BRACKET
block|,
literal|"}"
block|,
comment|// Folded result
literal|"âº"
comment|// U+207A: SUPERSCRIPT PLUS SIGN
operator|+
literal|"â"
comment|// U+208A: SUBSCRIPT PLUS SIGN
operator|+
literal|"ï¼"
comment|// U+FF0B: FULLWIDTH PLUS SIGN
block|,
literal|"+"
block|,
comment|// Folded result
literal|"â¼"
comment|// U+207C: SUPERSCRIPT EQUALS SIGN
operator|+
literal|"â"
comment|// U+208C: SUBSCRIPT EQUALS SIGN
operator|+
literal|"ï¼"
comment|// U+FF1D: FULLWIDTH EQUALS SIGN
block|,
literal|"="
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF01: FULLWIDTH EXCLAMATION MARK
block|,
literal|"!"
block|,
comment|// Folded result
literal|"â¼"
comment|// U+203C: DOUBLE EXCLAMATION MARK
block|,
literal|"!!"
block|,
comment|// Folded result
literal|"â"
comment|// U+2049: EXCLAMATION QUESTION MARK
block|,
literal|"!?"
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF03: FULLWIDTH NUMBER SIGN
block|,
literal|"#"
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF04: FULLWIDTH DOLLAR SIGN
block|,
literal|"$"
block|,
comment|// Folded result
literal|"â"
comment|// U+2052: COMMERCIAL MINUS SIGN
operator|+
literal|"ï¼"
comment|// U+FF05: FULLWIDTH PERCENT SIGN
block|,
literal|"%"
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF06: FULLWIDTH AMPERSAND
block|,
literal|"&"
block|,
comment|// Folded result
literal|"â"
comment|// U+204E: LOW ASTERISK
operator|+
literal|"ï¼"
comment|// U+FF0A: FULLWIDTH ASTERISK
block|,
literal|"*"
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF0C: FULLWIDTH COMMA
block|,
literal|","
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF0E: FULLWIDTH FULL STOP
block|,
literal|"."
block|,
comment|// Folded result
literal|"â"
comment|// U+2044: FRACTION SLASH
operator|+
literal|"ï¼"
comment|// U+FF0F: FULLWIDTH SOLIDUS
block|,
literal|"/"
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF1A: FULLWIDTH COLON
block|,
literal|":"
block|,
comment|// Folded result
literal|"â"
comment|// U+204F: REVERSED SEMICOLON
operator|+
literal|"ï¼"
comment|// U+FF1B: FULLWIDTH SEMICOLON
block|,
literal|";"
block|,
comment|// Folded result
literal|"ï¼"
comment|// U+FF1F: FULLWIDTH QUESTION MARK
block|,
literal|"?"
block|,
comment|// Folded result
literal|"â"
comment|// U+2047: DOUBLE QUESTION MARK
block|,
literal|"??"
block|,
comment|// Folded result
literal|"â"
comment|// U+2048: QUESTION EXCLAMATION MARK
block|,
literal|"?!"
block|,
comment|// Folded result
literal|"ï¼ "
comment|// U+FF20: FULLWIDTH COMMERCIAL AT
block|,
literal|"@"
block|,
comment|// Folded result
literal|"ï¼¼"
comment|// U+FF3C: FULLWIDTH REVERSE SOLIDUS
block|,
literal|"\\"
block|,
comment|// Folded result
literal|"â¸"
comment|// U+2038: CARET
operator|+
literal|"ï¼¾"
comment|// U+FF3E: FULLWIDTH CIRCUMFLEX ACCENT
block|,
literal|"^"
block|,
comment|// Folded result
literal|"ï¼¿"
comment|// U+FF3F: FULLWIDTH LOW LINE
block|,
literal|"_"
block|,
comment|// Folded result
literal|"â"
comment|// U+2053: SWUNG DASH
operator|+
literal|"ï½"
comment|// U+FF5E: FULLWIDTH TILDE
block|,
literal|"~"
block|,
comment|// Folded result
block|}
decl_stmt|;
comment|// Construct input text and expected output tokens
name|List
argument_list|<
name|String
argument_list|>
name|expectedUnfoldedTokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedFoldedTokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|inputText
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|foldings
operator|.
name|length
condition|;
name|n
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|inputText
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// Space between tokens
block|}
name|inputText
operator|.
name|append
argument_list|(
name|foldings
index|[
name|n
index|]
argument_list|)
expr_stmt|;
comment|// Construct the expected output tokens: both the unfolded and folded string,
comment|// with the folded duplicated as many times as the number of characters in
comment|// the input text.
name|StringBuilder
name|expected
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numChars
init|=
name|foldings
index|[
name|n
index|]
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|numChars
condition|;
operator|++
name|m
control|)
block|{
name|expected
operator|.
name|append
argument_list|(
name|foldings
index|[
name|n
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|expectedUnfoldedTokens
operator|.
name|add
argument_list|(
name|foldings
index|[
name|n
index|]
argument_list|)
expr_stmt|;
name|expectedFoldedTokens
operator|.
name|add
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|inputText
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ASCIIFoldingFilter
name|filter
init|=
operator|new
name|ASCIIFoldingFilter
argument_list|(
name|stream
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|unfoldedIter
init|=
name|expectedUnfoldedTokens
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|foldedIter
init|=
name|expectedFoldedTokens
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|foldedIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertNextTerms
argument_list|(
name|unfoldedIter
operator|.
name|next
argument_list|()
argument_list|,
name|foldedIter
operator|.
name|next
argument_list|()
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
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
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ASCIIFoldingFilter
argument_list|(
name|tokenizer
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ASCIIFoldingFilter
argument_list|(
name|tokenizer
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

