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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestRegExp
specifier|public
class|class
name|TestRegExp
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Simple smoke test for regular expression.    */
DECL|method|testSmoke
specifier|public
name|void
name|testSmoke
parameter_list|()
block|{
name|RegExp
name|r
init|=
operator|new
name|RegExp
argument_list|(
literal|"a(b+|c+)d"
argument_list|)
decl_stmt|;
name|Automaton
name|a
init|=
name|r
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|CharacterRunAutomaton
name|run
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|run
operator|.
name|run
argument_list|(
literal|"abbbbbd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|run
operator|.
name|run
argument_list|(
literal|"acd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|run
operator|.
name|run
argument_list|(
literal|"ad"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compiles a regular expression that is prohibitively expensive to    * determinize and expexts to catch an exception for it.    */
DECL|method|testDeterminizeTooManyStates
specifier|public
name|void
name|testDeterminizeTooManyStates
parameter_list|()
block|{
comment|// LUCENE-6046
name|String
name|source
init|=
literal|"[ac]*a[ac]{50,200}"
decl_stmt|;
try|try
block|{
operator|new
name|RegExp
argument_list|(
name|source
argument_list|)
operator|.
name|toAutomaton
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TooComplexToDeterminizeException
name|e
parameter_list|)
block|{
assert|assert
operator|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|source
argument_list|)
operator|)
assert|;
block|}
block|}
comment|// LUCENE-6046
DECL|method|testRepeatWithEmptyString
specifier|public
name|void
name|testRepeatWithEmptyString
parameter_list|()
throws|throws
name|Exception
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"[^y]*{1,2}"
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// paranoia:
name|assertTrue
argument_list|(
name|a
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

