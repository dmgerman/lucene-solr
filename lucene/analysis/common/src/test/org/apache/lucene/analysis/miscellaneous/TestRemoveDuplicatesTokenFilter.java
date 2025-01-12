begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Token
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
name|synonym
operator|.
name|SynonymFilter
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
name|synonym
operator|.
name|SynonymMap
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
name|OffsetAttribute
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
name|PositionIncrementAttribute
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
name|util
operator|.
name|CharsRef
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
name|TestUtil
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestRemoveDuplicatesTokenFilter
specifier|public
class|class
name|TestRemoveDuplicatesTokenFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|tok
specifier|public
specifier|static
name|Token
name|tok
parameter_list|(
name|int
name|pos
parameter_list|,
name|String
name|t
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|(
name|t
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|tok
return|;
block|}
DECL|method|tok
specifier|public
specifier|static
name|Token
name|tok
parameter_list|(
name|int
name|pos
parameter_list|,
name|String
name|t
parameter_list|)
block|{
return|return
name|tok
argument_list|(
name|pos
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|testDups
specifier|public
name|void
name|testDups
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|,
specifier|final
name|Token
modifier|...
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Iterator
argument_list|<
name|Token
argument_list|>
name|toks
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|tokens
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|TokenStream
name|ts
init|=
operator|new
name|RemoveDuplicatesTokenFilter
argument_list|(
operator|(
operator|new
name|TokenStream
argument_list|()
block|{
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|toks
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|Token
name|tok
init|=
name|toks
operator|.
name|next
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tok
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|tok
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
operator|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|expected
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoDups
specifier|public
name|void
name|testNoDups
parameter_list|()
throws|throws
name|Exception
block|{
name|testDups
argument_list|(
literal|"A B B C D E"
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"A"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"B"
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"B"
argument_list|,
literal|11
argument_list|,
literal|15
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"C"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"D"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"E"
argument_list|,
literal|21
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleDups
specifier|public
name|void
name|testSimpleDups
parameter_list|()
throws|throws
name|Exception
block|{
name|testDups
argument_list|(
literal|"A B C D E"
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"A"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"B"
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"B"
argument_list|,
literal|11
argument_list|,
literal|15
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"C"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"D"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"E"
argument_list|,
literal|21
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplexDups
specifier|public
name|void
name|testComplexDups
parameter_list|()
throws|throws
name|Exception
block|{
name|testDups
argument_list|(
literal|"A B C D E F G H I J K"
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"A"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"B"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"B"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"C"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"D"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"D"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"D"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"E"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"F"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"F"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"G"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"H"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"H"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"I"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"J"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"K"
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"J"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// some helper methods for the below test with synonyms
DECL|method|randomNonEmptyString
specifier|private
name|String
name|randomNonEmptyString
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
name|s
operator|.
name|indexOf
argument_list|(
literal|'\u0000'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|SynonymMap
operator|.
name|Builder
name|b
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|output
parameter_list|,
name|boolean
name|keepOrig
parameter_list|)
block|{
name|b
operator|.
name|add
argument_list|(
operator|new
name|CharsRef
argument_list|(
name|input
operator|.
name|replaceAll
argument_list|(
literal|" +"
argument_list|,
literal|"\u0000"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|CharsRef
argument_list|(
name|output
operator|.
name|replaceAll
argument_list|(
literal|" +"
argument_list|,
literal|"\u0000"
argument_list|)
argument_list|)
argument_list|,
name|keepOrig
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
specifier|final
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|10
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|SynonymMap
operator|.
name|Builder
name|b
init|=
operator|new
name|SynonymMap
operator|.
name|Builder
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numEntries
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numEntries
condition|;
name|j
operator|++
control|)
block|{
name|add
argument_list|(
name|b
argument_list|,
name|randomNonEmptyString
argument_list|()
argument_list|,
name|randomNonEmptyString
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SynonymMap
name|map
init|=
name|b
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|ignoreCase
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Analyzer
name|analyzer
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
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|SynonymFilter
argument_list|(
name|tokenizer
argument_list|,
name|map
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|RemoveDuplicatesTokenFilter
argument_list|(
name|stream
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
name|analyzer
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|RemoveDuplicatesTokenFilter
argument_list|(
name|tokenizer
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

