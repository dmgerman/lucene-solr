begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|Random
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
name|BytesRef
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Not thorough, but tries to test determinism correctness  * somewhat randomly, by determinizing a huge random lexicon.  */
end_comment

begin_class
DECL|class|TestDeterminizeLexicon
specifier|public
class|class
name|TestDeterminizeLexicon
extends|extends
name|LuceneTestCase
block|{
DECL|field|automata
specifier|private
name|List
argument_list|<
name|Automaton
argument_list|>
name|automata
init|=
operator|new
name|ArrayList
argument_list|<
name|Automaton
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|terms
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|method|testLexicon
specifier|public
name|void
name|testLexicon
parameter_list|()
block|{
name|random
operator|=
name|newRandom
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|automata
operator|.
name|clear
argument_list|()
expr_stmt|;
name|terms
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5000
condition|;
name|j
operator|++
control|)
block|{
name|String
name|randomString
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|randomString
argument_list|)
expr_stmt|;
name|automata
operator|.
name|add
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|randomString
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertLexicon
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertLexicon
specifier|public
name|void
name|assertLexicon
parameter_list|()
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|automata
argument_list|,
name|random
argument_list|)
expr_stmt|;
specifier|final
name|Automaton
name|lex
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|automata
argument_list|)
decl_stmt|;
name|lex
operator|.
name|determinize
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|lex
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|terms
control|)
block|{
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|lex
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ByteRunAutomaton
name|lexByte
init|=
operator|new
name|ByteRunAutomaton
argument_list|(
name|lex
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|terms
control|)
block|{
name|BytesRef
name|termByte
init|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|lexByte
operator|.
name|run
argument_list|(
name|termByte
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|termByte
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

