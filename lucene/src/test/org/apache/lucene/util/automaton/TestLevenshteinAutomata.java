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
name|List
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

begin_class
DECL|class|TestLevenshteinAutomata
specifier|public
class|class
name|TestLevenshteinAutomata
extends|extends
name|LuceneTestCase
block|{
DECL|method|testLev0
specifier|public
name|void
name|testLev0
parameter_list|()
throws|throws
name|Exception
block|{
name|assertLev
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertCharVectors
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testLev1
specifier|public
name|void
name|testLev1
parameter_list|()
throws|throws
name|Exception
block|{
name|assertLev
argument_list|(
literal|""
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertCharVectors
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testLev2
specifier|public
name|void
name|testLev2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertLev
argument_list|(
literal|""
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertCharVectors
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3094
DECL|method|testNoWastedStates
specifier|public
name|void
name|testNoWastedStates
parameter_list|()
throws|throws
name|Exception
block|{
name|AutomatonTestUtil
operator|.
name|assertNoDetachedStates
argument_list|(
operator|new
name|LevenshteinAutomata
argument_list|(
literal|"abc"
argument_list|,
literal|false
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Tests all possible characteristic vectors for some n    * This exhaustively tests the parametric transitions tables.    */
DECL|method|assertCharVectors
specifier|private
name|void
name|assertCharVectors
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|int
name|k
init|=
literal|2
operator|*
name|n
operator|+
literal|1
decl_stmt|;
comment|// use k + 2 as the exponent: the formula generates different transitions
comment|// for w, w-1, w-2
name|int
name|limit
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|k
operator|+
literal|2
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|String
name|encoded
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertLev
argument_list|(
name|encoded
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Builds a DFA for some string, and checks all Lev automata    * up to some maximum distance.    */
DECL|method|assertLev
specifier|private
name|void
name|assertLev
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|maxDistance
parameter_list|)
block|{
name|LevenshteinAutomata
name|builder
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|s
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|LevenshteinAutomata
name|tbuilder
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|s
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Automaton
name|automata
index|[]
init|=
operator|new
name|Automaton
index|[
name|maxDistance
operator|+
literal|1
index|]
decl_stmt|;
name|Automaton
name|tautomata
index|[]
init|=
operator|new
name|Automaton
index|[
name|maxDistance
operator|+
literal|1
index|]
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
name|automata
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|automata
index|[
name|n
index|]
operator|=
name|builder
operator|.
name|toAutomaton
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|tautomata
index|[
name|n
index|]
operator|=
name|tbuilder
operator|.
name|toAutomaton
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|automata
index|[
name|n
index|]
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|tautomata
index|[
name|n
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|automata
index|[
name|n
index|]
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tautomata
index|[
name|n
index|]
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|automata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|tautomata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|AutomatonTestUtil
operator|.
name|assertNoDetachedStates
argument_list|(
name|automata
index|[
name|n
index|]
argument_list|)
expr_stmt|;
name|AutomatonTestUtil
operator|.
name|assertNoDetachedStates
argument_list|(
name|tautomata
index|[
name|n
index|]
argument_list|)
expr_stmt|;
comment|// check that the dfa for n-1 accepts a subset of the dfa for n
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|automata
index|[
name|n
operator|-
literal|1
index|]
operator|.
name|subsetOf
argument_list|(
name|automata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|automata
index|[
name|n
operator|-
literal|1
index|]
operator|.
name|subsetOf
argument_list|(
name|tautomata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tautomata
index|[
name|n
operator|-
literal|1
index|]
operator|.
name|subsetOf
argument_list|(
name|automata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tautomata
index|[
name|n
operator|-
literal|1
index|]
operator|.
name|subsetOf
argument_list|(
name|tautomata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|automata
index|[
name|n
operator|-
literal|1
index|]
argument_list|,
name|automata
index|[
name|n
index|]
argument_list|)
expr_stmt|;
block|}
comment|// check that Lev(N) is a subset of LevT(N)
name|assertTrue
argument_list|(
name|automata
index|[
name|n
index|]
operator|.
name|subsetOf
argument_list|(
name|tautomata
index|[
name|n
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// special checks for specific n
switch|switch
condition|(
name|n
condition|)
block|{
case|case
literal|0
case|:
comment|// easy, matches the string itself
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
argument_list|)
argument_list|,
name|automata
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
argument_list|)
argument_list|,
name|tautomata
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
comment|// generate a lev1 naively, and check the accepted lang is the same.
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|naiveLev1
argument_list|(
name|s
argument_list|)
argument_list|,
name|automata
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|naiveLev1T
argument_list|(
name|s
argument_list|)
argument_list|,
name|tautomata
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|assertBruteForce
argument_list|(
name|s
argument_list|,
name|automata
index|[
name|n
index|]
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertBruteForceT
argument_list|(
name|s
argument_list|,
name|tautomata
index|[
name|n
index|]
argument_list|,
name|n
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * Return an automaton that accepts all 1-character insertions, deletions, and    * substitutions of s.    */
DECL|method|naiveLev1
specifier|private
name|Automaton
name|naiveLev1
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Automaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|a
argument_list|,
name|insertionsOf
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|a
argument_list|,
name|deletionsOf
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|a
argument_list|,
name|substitutionsOf
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Return an automaton that accepts all 1-character insertions, deletions,    * substitutions, and transpositions of s.    */
DECL|method|naiveLev1T
specifier|private
name|Automaton
name|naiveLev1T
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Automaton
name|a
init|=
name|naiveLev1
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|a
argument_list|,
name|transpositionsOf
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Return an automaton that accepts all 1-character insertions of s (inserting    * one character)    */
DECL|method|insertionsOf
specifier|private
name|Automaton
name|insertionsOf
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|List
argument_list|<
name|Automaton
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Automaton
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyChar
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|a
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Return an automaton that accepts all 1-character deletions of s (deleting    * one character).    */
DECL|method|deletionsOf
specifier|private
name|Automaton
name|deletionsOf
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|List
argument_list|<
name|Automaton
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Automaton
argument_list|>
argument_list|()
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|expandSingleton
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|a
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Return an automaton that accepts all 1-character substitutions of s    * (replacing one character)    */
DECL|method|substitutionsOf
specifier|private
name|Automaton
name|substitutionsOf
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|List
argument_list|<
name|Automaton
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Automaton
argument_list|>
argument_list|()
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyChar
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|a
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Return an automaton that accepts all transpositions of s    * (transposing two adjacent characters)    */
DECL|method|transpositionsOf
specifier|private
name|Automaton
name|transpositionsOf
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
literal|2
condition|)
return|return
name|BasicAutomata
operator|.
name|makeEmpty
argument_list|()
return|;
name|List
argument_list|<
name|Automaton
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Automaton
argument_list|>
argument_list|()
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
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|2
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|st
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|st
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
name|list
operator|.
name|add
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|st
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|a
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
DECL|method|assertBruteForce
specifier|private
name|void
name|assertBruteForce
parameter_list|(
name|String
name|input
parameter_list|,
name|Automaton
name|dfa
parameter_list|,
name|int
name|distance
parameter_list|)
block|{
name|CharacterRunAutomaton
name|ra
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|dfa
argument_list|)
decl_stmt|;
name|int
name|maxLen
init|=
name|input
operator|.
name|length
argument_list|()
operator|+
name|distance
operator|+
literal|1
decl_stmt|;
name|int
name|maxNum
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|maxLen
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
name|maxNum
condition|;
name|i
operator|++
control|)
block|{
name|String
name|encoded
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|boolean
name|accepts
init|=
name|ra
operator|.
name|run
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
if|if
condition|(
name|accepts
condition|)
block|{
name|assertTrue
argument_list|(
name|getDistance
argument_list|(
name|input
argument_list|,
name|encoded
argument_list|)
operator|<=
name|distance
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|getDistance
argument_list|(
name|input
argument_list|,
name|encoded
argument_list|)
operator|>
name|distance
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertBruteForceT
specifier|private
name|void
name|assertBruteForceT
parameter_list|(
name|String
name|input
parameter_list|,
name|Automaton
name|dfa
parameter_list|,
name|int
name|distance
parameter_list|)
block|{
name|CharacterRunAutomaton
name|ra
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|dfa
argument_list|)
decl_stmt|;
name|int
name|maxLen
init|=
name|input
operator|.
name|length
argument_list|()
operator|+
name|distance
operator|+
literal|1
decl_stmt|;
name|int
name|maxNum
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|maxLen
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
name|maxNum
condition|;
name|i
operator|++
control|)
block|{
name|String
name|encoded
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|boolean
name|accepts
init|=
name|ra
operator|.
name|run
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
if|if
condition|(
name|accepts
condition|)
block|{
name|assertTrue
argument_list|(
name|getTDistance
argument_list|(
name|input
argument_list|,
name|encoded
argument_list|)
operator|<=
name|distance
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|getTDistance
argument_list|(
name|input
argument_list|,
name|encoded
argument_list|)
operator|>
name|distance
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//*****************************
comment|// Compute Levenshtein distance: see org.apache.commons.lang.StringUtils#getLevenshteinDistance(String, String)
comment|//*****************************
DECL|method|getDistance
specifier|private
name|int
name|getDistance
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|other
parameter_list|)
block|{
name|char
index|[]
name|sa
decl_stmt|;
name|int
name|n
decl_stmt|;
name|int
name|p
index|[]
decl_stmt|;
comment|//'previous' cost array, horizontally
name|int
name|d
index|[]
decl_stmt|;
comment|// cost array, horizontally
name|int
name|_d
index|[]
decl_stmt|;
comment|//placeholder to assist in swapping p and d
comment|/*          The difference between this impl. and the previous is that, rather          than creating and retaining a matrix of size s.length()+1 by t.length()+1,          we maintain two single-dimensional arrays of length s.length()+1.  The first, d,          is the 'current working' distance array that maintains the newest distance cost          counts as we iterate through the characters of String s.  Each time we increment          the index of String t we are comparing, d is copied to p, the second int[].  Doing so          allows us to retain the previous cost counts as required by the algorithm (taking          the minimum of the cost count to the left, up one, and diagonally up and to the left          of the current cost count being calculated).  (Note that the arrays aren't really          copied anymore, just switched...this is clearly much better than cloning an array          or doing a System.arraycopy() each time  through the outer loop.)           Effectively, the difference between the two implementations is this one does not          cause an out of memory condition when calculating the LD over two very large strings.        */
name|sa
operator|=
name|target
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|n
operator|=
name|sa
operator|.
name|length
expr_stmt|;
name|p
operator|=
operator|new
name|int
index|[
name|n
operator|+
literal|1
index|]
expr_stmt|;
name|d
operator|=
operator|new
name|int
index|[
name|n
operator|+
literal|1
index|]
expr_stmt|;
specifier|final
name|int
name|m
init|=
name|other
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
operator|||
name|m
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|==
name|m
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|n
argument_list|,
name|m
argument_list|)
return|;
block|}
block|}
comment|// indexes into strings s and t
name|int
name|i
decl_stmt|;
comment|// iterates through s
name|int
name|j
decl_stmt|;
comment|// iterates through t
name|char
name|t_j
decl_stmt|;
comment|// jth character of t
name|int
name|cost
decl_stmt|;
comment|// cost
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|p
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
name|t_j
operator|=
name|other
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
expr_stmt|;
name|d
index|[
literal|0
index|]
operator|=
name|j
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|cost
operator|=
name|sa
index|[
name|i
operator|-
literal|1
index|]
operator|==
name|t_j
condition|?
literal|0
else|:
literal|1
expr_stmt|;
comment|// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
name|d
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|,
name|p
index|[
name|i
index|]
operator|+
literal|1
argument_list|)
argument_list|,
name|p
index|[
name|i
operator|-
literal|1
index|]
operator|+
name|cost
argument_list|)
expr_stmt|;
block|}
comment|// copy current distance counts to 'previous row' distance counts
name|_d
operator|=
name|p
expr_stmt|;
name|p
operator|=
name|d
expr_stmt|;
name|d
operator|=
name|_d
expr_stmt|;
block|}
comment|// our last action in the above loop was to switch d and p, so p now
comment|// actually has the most recent cost counts
return|return
name|Math
operator|.
name|abs
argument_list|(
name|p
index|[
name|n
index|]
argument_list|)
return|;
block|}
DECL|method|getTDistance
specifier|private
name|int
name|getTDistance
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|other
parameter_list|)
block|{
name|char
index|[]
name|sa
decl_stmt|;
name|int
name|n
decl_stmt|;
name|int
name|d
index|[]
index|[]
decl_stmt|;
comment|// cost array
name|sa
operator|=
name|target
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|n
operator|=
name|sa
operator|.
name|length
expr_stmt|;
specifier|final
name|int
name|m
init|=
name|other
operator|.
name|length
argument_list|()
decl_stmt|;
name|d
operator|=
operator|new
name|int
index|[
name|n
operator|+
literal|1
index|]
index|[
name|m
operator|+
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
operator|||
name|m
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|==
name|m
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|n
argument_list|,
name|m
argument_list|)
return|;
block|}
block|}
comment|// indexes into strings s and t
name|int
name|i
decl_stmt|;
comment|// iterates through s
name|int
name|j
decl_stmt|;
comment|// iterates through t
name|char
name|t_j
decl_stmt|;
comment|// jth character of t
name|int
name|cost
decl_stmt|;
comment|// cost
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|d
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
name|i
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
name|d
index|[
literal|0
index|]
index|[
name|j
index|]
operator|=
name|j
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
name|t_j
operator|=
name|other
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|cost
operator|=
name|sa
index|[
name|i
operator|-
literal|1
index|]
operator|==
name|t_j
condition|?
literal|0
else|:
literal|1
expr_stmt|;
comment|// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
index|]
operator|+
literal|1
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|)
argument_list|,
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
operator|-
literal|1
index|]
operator|+
name|cost
argument_list|)
expr_stmt|;
comment|// transposition
if|if
condition|(
name|i
operator|>
literal|1
operator|&&
name|j
operator|>
literal|1
operator|&&
name|target
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|==
name|other
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|2
argument_list|)
operator|&&
name|target
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|2
argument_list|)
operator|==
name|other
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
condition|)
block|{
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|d
index|[
name|i
operator|-
literal|2
index|]
index|[
name|j
operator|-
literal|2
index|]
operator|+
name|cost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// our last action in the above loop was to switch d and p, so p now
comment|// actually has the most recent cost counts
return|return
name|Math
operator|.
name|abs
argument_list|(
name|d
index|[
name|n
index|]
index|[
name|m
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

