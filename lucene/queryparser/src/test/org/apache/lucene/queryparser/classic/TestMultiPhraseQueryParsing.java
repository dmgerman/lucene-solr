begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.classic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|MultiPhraseQuery
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
name|search
operator|.
name|Query
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

begin_class
DECL|class|TestMultiPhraseQueryParsing
specifier|public
class|class
name|TestMultiPhraseQueryParsing
extends|extends
name|LuceneTestCase
block|{
DECL|class|TokenAndPos
specifier|private
specifier|static
class|class
name|TokenAndPos
block|{
DECL|field|token
specifier|public
specifier|final
name|String
name|token
decl_stmt|;
DECL|field|pos
specifier|public
specifier|final
name|int
name|pos
decl_stmt|;
DECL|method|TokenAndPos
specifier|public
name|TokenAndPos
parameter_list|(
name|String
name|token
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
block|}
DECL|class|CannedAnalyzer
specifier|private
specifier|static
class|class
name|CannedAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|tokens
specifier|private
specifier|final
name|TokenAndPos
index|[]
name|tokens
decl_stmt|;
DECL|method|CannedAnalyzer
specifier|public
name|CannedAnalyzer
parameter_list|(
name|TokenAndPos
index|[]
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|CannedTokenizer
argument_list|(
name|tokens
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|CannedTokenizer
specifier|private
specifier|static
class|class
name|CannedTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|tokens
specifier|private
specifier|final
name|TokenAndPos
index|[]
name|tokens
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
init|=
literal|0
decl_stmt|;
DECL|field|lastPos
specifier|private
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
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
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CannedTokenizer
specifier|public
name|CannedTokenizer
parameter_list|(
name|TokenAndPos
index|[]
name|tokens
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|upto
operator|<
name|tokens
operator|.
name|length
condition|)
block|{
specifier|final
name|TokenAndPos
name|token
init|=
name|tokens
index|[
name|upto
operator|++
index|]
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
name|token
operator|.
name|token
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|token
operator|.
name|pos
operator|-
name|lastPos
argument_list|)
expr_stmt|;
name|lastPos
operator|=
name|token
operator|.
name|pos
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
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|upto
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastPos
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|testMultiPhraseQueryParsing
specifier|public
name|void
name|testMultiPhraseQueryParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenAndPos
index|[]
name|INCR_0_QUERY_TOKENS_AND
init|=
operator|new
name|TokenAndPos
index|[]
block|{
operator|new
name|TokenAndPos
argument_list|(
literal|"a"
argument_list|,
literal|0
argument_list|)
block|,
operator|new
name|TokenAndPos
argument_list|(
literal|"1"
argument_list|,
literal|0
argument_list|)
block|,
operator|new
name|TokenAndPos
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
block|,
operator|new
name|TokenAndPos
argument_list|(
literal|"1"
argument_list|,
literal|1
argument_list|)
block|,
operator|new
name|TokenAndPos
argument_list|(
literal|"c"
argument_list|,
literal|2
argument_list|)
block|}
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"field"
argument_list|,
operator|new
name|CannedAnalyzer
argument_list|(
name|INCR_0_QUERY_TOKENS_AND
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
literal|"\"this text is acually ignored\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"wrong query type!"
argument_list|,
name|q
operator|instanceof
name|MultiPhraseQuery
argument_list|)
expr_stmt|;
name|MultiPhraseQuery
name|multiPhraseQuery
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|multiPhraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
block|}
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|multiPhraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
block|}
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|multiPhraseQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"c"
argument_list|)
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|multiPhraseQuery
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

