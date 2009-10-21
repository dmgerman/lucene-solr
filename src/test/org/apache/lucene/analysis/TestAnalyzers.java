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
name|StringReader
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
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|TermAttribute
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
name|index
operator|.
name|Payload
import|;
end_import

begin_class
DECL|class|TestAnalyzers
specifier|public
class|class
name|TestAnalyzers
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|TestAnalyzers
specifier|public
name|TestAnalyzers
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
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
literal|"foo.bar.FOO.BAR"
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
literal|"U.S.A."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"u"
block|,
literal|"s"
block|,
literal|"a"
block|}
argument_list|)
expr_stmt|;
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
literal|"B2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"b"
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
literal|"b"
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
DECL|method|testNull
specifier|public
name|void
name|testNull
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
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
literal|"FOO"
block|,
literal|"BAR"
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
literal|"."
block|,
literal|"FOO"
block|,
literal|"<>"
block|,
literal|"BAR"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo.bar.FOO.BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo.bar.FOO.BAR"
block|}
argument_list|)
expr_stmt|;
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
literal|"U.S.A."
block|}
argument_list|)
expr_stmt|;
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
literal|"C++"
block|}
argument_list|)
expr_stmt|;
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
literal|"B2B"
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
literal|"2B"
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
literal|"\"QUOTED\""
block|,
literal|"word"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStop
specifier|public
name|void
name|testStop
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|StopAnalyzer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
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
literal|"foo a bar such FOO THESE BAR"
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
block|}
DECL|method|verifyPayload
name|void
name|verifyPayload
parameter_list|(
name|TokenStream
name|ts
parameter_list|)
throws|throws
name|IOException
block|{
name|PayloadAttribute
name|payloadAtt
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|b
init|=
literal|1
init|;
condition|;
name|b
operator|++
control|)
block|{
name|boolean
name|hasNext
init|=
name|ts
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
break|break;
comment|// System.out.println("id="+System.identityHashCode(nextToken) + " " + t);
comment|// System.out.println("payload=" + (int)nextToken.getPayload().toByteArray()[0]);
name|assertEquals
argument_list|(
name|b
argument_list|,
name|payloadAtt
operator|.
name|getPayload
argument_list|()
operator|.
name|toByteArray
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Make sure old style next() calls result in a new copy of payloads
DECL|method|testPayloadCopy
specifier|public
name|void
name|testPayloadCopy
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|s
init|=
literal|"how now brown cow"
decl_stmt|;
name|TokenStream
name|ts
decl_stmt|;
name|ts
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|PayloadSetter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|verifyPayload
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|PayloadSetter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|verifyPayload
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1150: Just a compile time test, to ensure the
comment|// StandardAnalyzer constants remain publicly accessible
DECL|method|_testStandardConstants
specifier|public
name|void
name|_testStandardConstants
parameter_list|()
block|{
name|int
name|x
init|=
name|StandardTokenizer
operator|.
name|ALPHANUM
decl_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|APOSTROPHE
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|ACRONYM
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|COMPANY
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|EMAIL
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|HOST
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|NUM
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|CJ
expr_stmt|;
name|String
index|[]
name|y
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
decl_stmt|;
block|}
DECL|class|MyStandardAnalyzer
specifier|private
specifier|static
class|class
name|MyStandardAnalyzer
extends|extends
name|StandardAnalyzer
block|{
DECL|method|MyStandardAnalyzer
specifier|public
name|MyStandardAnalyzer
parameter_list|()
block|{
name|super
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|WhitespaceAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
DECL|method|testSubclassOverridingOnlyTokenStream
specifier|public
name|void
name|testSubclassOverridingOnlyTokenStream
parameter_list|()
throws|throws
name|Throwable
block|{
name|Analyzer
name|a
init|=
operator|new
name|MyStandardAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|a
operator|.
name|reusableTokenStream
argument_list|(
literal|"field"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"the"
argument_list|)
argument_list|)
decl_stmt|;
comment|// StandardAnalyzer will discard "the" (it's a
comment|// stopword), by my subclass will not:
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|PayloadSetter
class|class
name|PayloadSetter
extends|extends
name|TokenFilter
block|{
DECL|field|payloadAtt
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|method|PayloadSetter
specifier|public
name|PayloadSetter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|data
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|p
name|Payload
name|p
init|=
operator|new
name|Payload
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|hasNext
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
return|return
literal|false
return|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// reuse the payload / byte[]
name|data
index|[
literal|0
index|]
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

