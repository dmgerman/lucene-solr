begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|CannedTokenStream
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

begin_class
DECL|class|TestFlattenGraphFilter
specifier|public
class|class
name|TestFlattenGraphFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|token
specifier|private
specifier|static
name|Token
name|token
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|posInc
parameter_list|,
name|int
name|posLength
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
specifier|final
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
name|term
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|posInc
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionLength
argument_list|(
name|posLength
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
DECL|method|testSimpleMock
specifier|public
name|void
name|testSimpleMock
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
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|ts
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"wtf happened"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"wtf"
block|,
literal|"happened"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|12
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
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Make sure graph is unchanged if it's already flat
DECL|method|testAlreadyFlatten
specifier|public
name|void
name|testAlreadyFlatten
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|12
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"wtf"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"what"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"wow"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"the"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"that's"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"fudge"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"funny"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"happened"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|12
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... but on output, it's flattened to wtf/what/wow that's/the fudge/funny happened:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"wtf"
block|,
literal|"what"
block|,
literal|"wow"
block|,
literal|"the"
block|,
literal|"that's"
block|,
literal|"fudge"
block|,
literal|"funny"
block|,
literal|"happened"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
DECL|method|testWTF1
specifier|public
name|void
name|testWTF1
parameter_list|()
throws|throws
name|Exception
block|{
comment|// "wow that's funny" and "what the fudge" are separate side paths, in parallel with "wtf", on input:
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|12
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"wtf"
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"what"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"wow"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"the"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"fudge"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"that's"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"funny"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"happened"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|12
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... but on output, it's flattened to wtf/what/wow that's/the fudge/funny happened:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"wtf"
block|,
literal|"what"
block|,
literal|"wow"
block|,
literal|"the"
block|,
literal|"that's"
block|,
literal|"fudge"
block|,
literal|"funny"
block|,
literal|"happened"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
comment|/** Same as testWTF1 except the "wtf" token comes out later */
DECL|method|testWTF2
specifier|public
name|void
name|testWTF2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// "wow that's funny" and "what the fudge" are separate side paths, in parallel with "wtf", on input:
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|12
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"what"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"wow"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"wtf"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"the"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"fudge"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"that's"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"funny"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"happened"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|12
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... but on output, it's flattened to wtf/what/wow that's/the fudge/funny happened:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"what"
block|,
literal|"wow"
block|,
literal|"wtf"
block|,
literal|"the"
block|,
literal|"that's"
block|,
literal|"fudge"
block|,
literal|"funny"
block|,
literal|"happened"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|3
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|3
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonGreedySynonyms
specifier|public
name|void
name|testNonGreedySynonyms
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This is just "hypothetical" for Lucene today, because SynFilter is
comment|// greedy: when two syn rules match on overlapping tokens, only one
comment|// (greedily) wins.  This test pretends all syn matches could match:
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"wizard"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|token
argument_list|(
literal|"wizard_of_oz"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|12
argument_list|)
block|,
name|token
argument_list|(
literal|"of"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|,
literal|9
argument_list|)
block|,
name|token
argument_list|(
literal|"oz"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|)
block|,
name|token
argument_list|(
literal|"oz_screams"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
block|,
name|token
argument_list|(
literal|"screams"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|13
argument_list|,
literal|20
argument_list|)
block|,       }
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... but on output, it's flattened to wtf/what/wow that's/the fudge/funny happened:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"wizard"
block|,
literal|"wizard_of_oz"
block|,
literal|"of"
block|,
literal|"oz"
block|,
literal|"oz_screams"
block|,
literal|"screams"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|10
block|,
literal|10
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|12
block|,
literal|9
block|,
literal|12
block|,
literal|20
block|,
literal|20
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|}
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonGraph
specifier|public
name|void
name|testNonGraph
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|22
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"hello"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
block|,
name|token
argument_list|(
literal|"pseudo"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|,
literal|12
argument_list|)
block|,
name|token
argument_list|(
literal|"world"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|13
argument_list|,
literal|18
argument_list|)
block|,
name|token
argument_list|(
literal|"fun"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|19
argument_list|,
literal|22
argument_list|)
block|,       }
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... but on output, it's flattened to wtf/what/wow that's/the fudge/funny happened:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hello"
block|,
literal|"pseudo"
block|,
literal|"world"
block|,
literal|"fun"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|6
block|,
literal|13
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|12
block|,
literal|18
block|,
literal|22
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|22
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleHole
specifier|public
name|void
name|testSimpleHole
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|13
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"hello"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
block|,
name|token
argument_list|(
literal|"hole"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|,
literal|10
argument_list|)
block|,
name|token
argument_list|(
literal|"fun"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|)
block|,       }
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... but on output, it's flattened to wtf/what/wow that's/the fudge/funny happened:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hello"
block|,
literal|"hole"
block|,
literal|"fun"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|6
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|10
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|13
argument_list|)
expr_stmt|;
block|}
DECL|method|testHoleUnderSyn
specifier|public
name|void
name|testHoleUnderSyn
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests a StopFilter after SynFilter where a stopword in a syn is removed
comment|//
comment|//   wizard of oz -> woz syn, but then "of" becomes a hole
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|12
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"wizard"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|token
argument_list|(
literal|"woz"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|12
argument_list|)
block|,
name|token
argument_list|(
literal|"oz"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|)
block|,       }
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"wizard"
block|,
literal|"woz"
block|,
literal|"oz"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|10
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|12
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|1
block|}
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
DECL|method|testStrangelyNumberedNodes
specifier|public
name|void
name|testStrangelyNumberedNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Uses only nodes 0, 2, 3, i.e. 1 is just never used (it is not a hole!!)
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|27
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"dog"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
block|,
name|token
argument_list|(
literal|"puppy"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
block|,
name|token
argument_list|(
literal|"flies"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|,
literal|11
argument_list|)
block|,       }
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dog"
block|,
literal|"puppy"
block|,
literal|"flies"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|27
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoLongParallelPaths
specifier|public
name|void
name|testTwoLongParallelPaths
parameter_list|()
throws|throws
name|Exception
block|{
comment|// "a a a a a a" in parallel with "b b b b b b"
name|TokenStream
name|in
init|=
operator|new
name|CannedTokenStream
argument_list|(
literal|0
argument_list|,
literal|11
argument_list|,
operator|new
name|Token
index|[]
block|{
name|token
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
block|,
name|token
argument_list|(
literal|"b"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
block|,
name|token
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
block|,
name|token
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|)
block|,
name|token
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|)
block|,
name|token
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
block|,
name|token
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
block|,
name|token
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
block|,
name|token
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
block|,
name|token
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
block|,
name|token
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
block|,       }
argument_list|)
decl_stmt|;
name|TokenStream
name|out
init|=
operator|new
name|FlattenGraphFilter
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// ... becomes flattened to a single path with overlapping a/b token between each node:
name|assertTokenStreamContents
argument_list|(
name|out
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|2
block|,
literal|2
block|,
literal|4
block|,
literal|4
block|,
literal|6
block|,
literal|6
block|,
literal|8
block|,
literal|8
block|,
literal|10
block|,
literal|10
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|3
block|,
literal|3
block|,
literal|5
block|,
literal|5
block|,
literal|7
block|,
literal|7
block|,
literal|9
block|,
literal|9
block|,
literal|11
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|,
literal|11
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: TestSynonymGraphFilter's testRandomSyns also tests FlattenGraphFilter
block|}
end_class

end_unit

