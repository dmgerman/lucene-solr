begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|ByteArrayInputStream
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|RuleBasedCollator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|ResourceLoader
import|;
end_import

begin_class
DECL|class|TestCollationKeyFilterFactory
specifier|public
class|class
name|TestCollationKeyFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/*    * Turkish has some funny casing.    * This test shows how you can solve this kind of thing easily with collation.    * Instead of using LowerCaseFilter, use a turkish collator with primary strength.    * Then things will sort and match correctly.    */
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|turkishUpperCase
init|=
literal|"I WÄ°LL USE TURKÄ°SH CASING"
decl_stmt|;
name|String
name|turkishLowerCase
init|=
literal|"Ä± will use turkish casÄ±ng"
decl_stmt|;
name|CollationKeyFilterFactory
name|factory
init|=
operator|new
name|CollationKeyFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"language"
argument_list|,
literal|"tr"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"strength"
argument_list|,
literal|"primary"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|tsUpper
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|turkishUpperCase
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tsLower
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|turkishLowerCase
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|tsUpper
argument_list|,
name|tsLower
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test usage of the decomposition option for unicode normalization.    */
DECL|method|testNormalization
specifier|public
name|void
name|testNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|turkishUpperCase
init|=
literal|"I W\u0049\u0307LL USE TURKÄ°SH CASING"
decl_stmt|;
name|String
name|turkishLowerCase
init|=
literal|"Ä± will use turkish casÄ±ng"
decl_stmt|;
name|CollationKeyFilterFactory
name|factory
init|=
operator|new
name|CollationKeyFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"language"
argument_list|,
literal|"tr"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"strength"
argument_list|,
literal|"primary"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"decomposition"
argument_list|,
literal|"canonical"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|tsUpper
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|turkishUpperCase
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tsLower
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|turkishLowerCase
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|tsUpper
argument_list|,
name|tsLower
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test usage of the K decomposition option for unicode normalization.    * This works even with identical strength.    */
DECL|method|testFullDecomposition
specifier|public
name|void
name|testFullDecomposition
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|fullWidth
init|=
literal|"ï¼´ï½ï½ï½ï½ï½ï½"
decl_stmt|;
name|String
name|halfWidth
init|=
literal|"Testing"
decl_stmt|;
name|CollationKeyFilterFactory
name|factory
init|=
operator|new
name|CollationKeyFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"language"
argument_list|,
literal|"zh"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"strength"
argument_list|,
literal|"identical"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"decomposition"
argument_list|,
literal|"full"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|tsFull
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|fullWidth
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tsHalf
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|halfWidth
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|tsFull
argument_list|,
name|tsHalf
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test secondary strength, for english case is not significant.    */
DECL|method|testSecondaryStrength
specifier|public
name|void
name|testSecondaryStrength
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|upperCase
init|=
literal|"TESTING"
decl_stmt|;
name|String
name|lowerCase
init|=
literal|"testing"
decl_stmt|;
name|CollationKeyFilterFactory
name|factory
init|=
operator|new
name|CollationKeyFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"language"
argument_list|,
literal|"en"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"strength"
argument_list|,
literal|"secondary"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"decomposition"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|tsUpper
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|upperCase
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tsLower
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|lowerCase
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|tsUpper
argument_list|,
name|tsLower
argument_list|)
expr_stmt|;
block|}
comment|/*    * For german, you might want oe to sort and match with o umlaut.    * This is not the default, but you can make a customized ruleset to do this.    *    * The default is DIN 5007-1, this shows how to tailor a collator to get DIN 5007-2 behavior.    *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4423383    */
DECL|method|testCustomRules
specifier|public
name|void
name|testCustomRules
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleBasedCollator
name|baseCollator
init|=
operator|(
name|RuleBasedCollator
operator|)
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"de"
argument_list|,
literal|"DE"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|DIN5007_2_tailorings
init|=
literal|"& ae , a\u0308& AE , A\u0308"
operator|+
literal|"& oe , o\u0308& OE , O\u0308"
operator|+
literal|"& ue , u\u0308& UE , u\u0308"
decl_stmt|;
name|RuleBasedCollator
name|tailoredCollator
init|=
operator|new
name|RuleBasedCollator
argument_list|(
name|baseCollator
operator|.
name|getRules
argument_list|()
operator|+
name|DIN5007_2_tailorings
argument_list|)
decl_stmt|;
name|String
name|tailoredRules
init|=
name|tailoredCollator
operator|.
name|getRules
argument_list|()
decl_stmt|;
comment|//
comment|// at this point, you would save these tailoredRules to a file,
comment|// and use the custom parameter.
comment|//
name|String
name|germanUmlaut
init|=
literal|"TÃ¶ne"
decl_stmt|;
name|String
name|germanOE
init|=
literal|"Toene"
decl_stmt|;
name|CollationKeyFilterFactory
name|factory
init|=
operator|new
name|CollationKeyFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"custom"
argument_list|,
literal|"rules.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"strength"
argument_list|,
literal|"primary"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
name|tailoredRules
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|tsUmlaut
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|germanUmlaut
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tsOE
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|germanOE
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertCollatesToSame
argument_list|(
name|tsUmlaut
argument_list|,
name|tsOE
argument_list|)
expr_stmt|;
block|}
DECL|class|StringMockSolrResourceLoader
specifier|private
class|class
name|StringMockSolrResourceLoader
implements|implements
name|ResourceLoader
block|{
DECL|field|text
name|String
name|text
decl_stmt|;
DECL|method|StringMockSolrResourceLoader
name|StringMockSolrResourceLoader
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
DECL|method|getLines
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|newInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|,
name|String
modifier|...
name|subpackages
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|assertCollatesToSame
specifier|private
name|void
name|assertCollatesToSame
parameter_list|(
name|TokenStream
name|stream1
parameter_list|,
name|TokenStream
name|stream2
parameter_list|)
throws|throws
name|IOException
block|{
name|stream1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stream2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CharTermAttribute
name|term1
init|=
name|stream1
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|term2
init|=
name|stream2
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stream1
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stream2
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|term1
operator|.
name|toString
argument_list|()
argument_list|,
name|term2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream1
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream2
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|stream1
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream2
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream1
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

