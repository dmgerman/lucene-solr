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

begin_class
DECL|class|TestSpecialOperations
specifier|public
class|class
name|TestSpecialOperations
extends|extends
name|LuceneTestCase
block|{
comment|/**    * tests against the original brics implementation.    */
DECL|method|testIsFinite
specifier|public
name|void
name|testIsFinite
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|200
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|LightAutomaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|AutomatonTestUtil
operator|.
name|isFiniteSlow
argument_list|(
name|a
argument_list|)
argument_list|,
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Pass false for testRecursive if the expected strings    *  may be too long */
DECL|method|getFiniteStrings
specifier|private
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|LightAutomaton
name|a
parameter_list|,
name|int
name|limit
parameter_list|,
name|boolean
name|testRecursive
parameter_list|)
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|testRecursive
condition|)
block|{
name|assertEquals
argument_list|(
name|AutomatonTestUtil
operator|.
name|getFiniteStringsRecursiveLight
argument_list|(
name|a
argument_list|,
name|limit
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Basic test for getFiniteStrings    */
DECL|method|testFiniteStringsBasic
specifier|public
name|void
name|testFiniteStringsBasic
parameter_list|()
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|unionLight
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"dog"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"duck"
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|MinimizationOperationsLight
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|strings
init|=
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|strings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|dog
init|=
operator|new
name|IntsRef
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
name|strings
operator|.
name|contains
argument_list|(
name|dog
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRef
name|duck
init|=
operator|new
name|IntsRef
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
name|strings
operator|.
name|contains
argument_list|(
name|duck
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
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|unionLight
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
name|bigString1
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
name|bigString2
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|strings
init|=
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|strings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
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
name|strings
operator|.
name|contains
argument_list|(
name|scratch
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
name|strings
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
argument_list|<
name|IntsRef
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LightAutomaton
argument_list|>
name|automata
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|automata
operator|.
name|add
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
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
name|strings
operator|.
name|add
argument_list|(
name|scratch
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
comment|// TODO: we could sometimes use
comment|// DaciukMihovAutomatonBuilder here
comment|// TODO: what other random things can we do here...
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|unionLight
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
name|MinimizationOperationsLight
operator|.
name|minimize
argument_list|(
name|a
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
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
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
name|BasicOperations
operator|.
name|removeDeadStates
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
operator|==
literal|false
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
DECL|method|testWithCycle
specifier|public
name|void
name|testWithCycle
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
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
name|toLightAutomaton
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testRandomFiniteStrings2
specifier|public
name|void
name|testRandomFiniteStrings2
parameter_list|()
block|{
comment|// Just makes sure we can run on any random finite
comment|// automaton:
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|LightAutomaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Must pass a limit because the random automaton
comment|// can accept MANY strings:
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// NOTE: cannot do this, because the method is not
comment|// guaranteed to detect cycles when you have a limit
comment|//assertTrue(SpecialOperations.isFinite(a));
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testInvalidLimit
specifier|public
name|void
name|testInvalidLimit
parameter_list|()
block|{
name|LightAutomaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|7
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testInvalidLimit2
specifier|public
name|void
name|testInvalidLimit2
parameter_list|()
block|{
name|LightAutomaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testSingletonNoLimit
specifier|public
name|void
name|testSingletonNoLimit
parameter_list|()
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
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
name|result
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingletonLimit1
specifier|public
name|void
name|testSingletonLimit1
parameter_list|()
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
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
name|result
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

