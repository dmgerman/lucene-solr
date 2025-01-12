begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IntsRef
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
name|IntsRefBuilder
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
name|TestUtil
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
name|fst
operator|.
name|Util
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
import|;
end_import

begin_comment
comment|/**  * Test for {@link FiniteStringsIterator}.  */
end_comment

begin_class
DECL|class|FiniteStringsIteratorTest
specifier|public
class|class
name|FiniteStringsIteratorTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandomFiniteStrings1
specifier|public
name|void
name|testRandomFiniteStrings1
parameter_list|()
block|{
name|int
name|numStrings
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: numStrings="
operator|+
name|numStrings
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|strings
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Automaton
argument_list|>
name|automata
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|IntsRefBuilder
name|scratch
init|=
operator|new
name|IntsRefBuilder
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
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|strings
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|toIntsRef
argument_list|()
argument_list|)
condition|)
block|{
name|automata
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  add string="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// TODO: we could sometimes use
comment|// DaciukMihovAutomatonBuilder here
comment|// TODO: what other random things can we do here...
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|automata
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|a
operator|=
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: a.minimize numStates="
operator|+
name|a
operator|.
name|getNumStates
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: a.determinize"
argument_list|)
expr_stmt|;
block|}
name|a
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|a
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: a.removeDeadStates"
argument_list|)
expr_stmt|;
block|}
name|a
operator|=
name|Operations
operator|.
name|removeDeadStates
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|FiniteStringsIterator
name|iterator
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
name|assertFiniteStringsRecursive
argument_list|(
name|a
argument_list|,
name|actual
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|strings
operator|.
name|equals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|actual
argument_list|)
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"strings.size()="
operator|+
name|strings
operator|.
name|size
argument_list|()
operator|+
literal|" actual.size="
operator|+
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|x
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|y
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|actual
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|y
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|x
operator|.
name|size
argument_list|()
argument_list|,
name|y
operator|.
name|size
argument_list|()
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
name|end
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  i="
operator|+
name|i
operator|+
literal|" string="
operator|+
name|toString
argument_list|(
name|x
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|+
literal|" actual="
operator|+
name|toString
argument_list|(
name|y
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"wrong strings found"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Basic test for getFiniteStrings    */
DECL|method|testFiniteStringsBasic
specifier|public
name|void
name|testFiniteStringsBasic
parameter_list|()
block|{
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"dog"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"duck"
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
name|FiniteStringsIterator
name|iterator
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
name|assertFiniteStringsRecursive
argument_list|(
name|a
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|dog
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"dog"
argument_list|)
argument_list|,
name|dog
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|dog
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|duck
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"duck"
argument_list|)
argument_list|,
name|duck
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|duck
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFiniteStringsEatsStack
specifier|public
name|void
name|testFiniteStringsEatsStack
parameter_list|()
block|{
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|50000
index|]
decl_stmt|;
name|TestUtil
operator|.
name|randomFixedLengthUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|bigString1
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|randomFixedLengthUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|bigString2
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|bigString1
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
name|bigString2
argument_list|)
argument_list|)
decl_stmt|;
name|FiniteStringsIterator
name|iterator
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|scratch
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
name|bigString1
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bigString1
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
name|bigString2
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bigString2
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithCycle
specifier|public
name|void
name|testWithCycle
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"abc.*"
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|FiniteStringsIterator
name|iterator
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|getFiniteStrings
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingletonNoLimit
specifier|public
name|void
name|testSingletonNoLimit
parameter_list|()
block|{
name|Automaton
name|a
init|=
name|Automata
operator|.
name|makeString
argument_list|(
literal|"foobar"
argument_list|)
decl_stmt|;
name|FiniteStringsIterator
name|iterator
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|scratch
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
literal|"foobar"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testShortAccept
specifier|public
name|void
name|testShortAccept
parameter_list|()
block|{
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"xy"
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
name|FiniteStringsIterator
name|iterator
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|x
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|x
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|xy
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"xy"
argument_list|)
argument_list|,
name|xy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
name|xy
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleString
specifier|public
name|void
name|testSingleString
parameter_list|()
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|end
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'a'
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|a
operator|.
name|finishState
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|accepted
init|=
name|TestOperations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|accepted
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRefBuilder
name|intsRef
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|intsRef
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|accepted
operator|.
name|contains
argument_list|(
name|intsRef
operator|.
name|toIntsRef
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * All strings generated by the iterator.    */
DECL|method|getFiniteStrings
specifier|static
name|List
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|FiniteStringsIterator
name|iterator
parameter_list|)
block|{
name|List
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IntsRef
name|finiteString
init|;
operator|(
name|finiteString
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|finiteString
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Check that strings the automaton returns are as expected.    *    * @param automaton Automaton.    * @param actual Strings generated by automaton.    */
DECL|method|assertFiniteStringsRecursive
specifier|private
name|void
name|assertFiniteStringsRecursive
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|List
argument_list|<
name|IntsRef
argument_list|>
name|actual
parameter_list|)
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|expected
init|=
name|AutomatonTestUtil
operator|.
name|getFiniteStringsRecursive
argument_list|(
name|automaton
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Check that no string is emitted twice.
name|assertEquals
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// ascii only!
DECL|method|toString
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|IntsRef
name|ints
parameter_list|)
block|{
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|(
name|ints
operator|.
name|length
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|br
operator|.
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|ints
operator|.
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
name|br
operator|.
name|length
operator|=
name|ints
operator|.
name|length
expr_stmt|;
return|return
name|br
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

