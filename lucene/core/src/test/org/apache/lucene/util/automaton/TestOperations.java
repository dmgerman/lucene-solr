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
name|java
operator|.
name|util
operator|.
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
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

begin_class
DECL|class|TestOperations
specifier|public
class|class
name|TestOperations
extends|extends
name|LuceneTestCase
block|{
comment|/** Test string union. */
DECL|method|testStringUnion
specifier|public
name|void
name|testStringUnion
parameter_list|()
block|{
name|List
argument_list|<
name|BytesRef
argument_list|>
name|strings
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
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|strings
argument_list|)
expr_stmt|;
name|Automaton
name|union
init|=
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|union
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Operations
operator|.
name|hasDeadStatesFromInitial
argument_list|(
name|union
argument_list|)
argument_list|)
expr_stmt|;
name|Automaton
name|naiveUnion
init|=
name|naiveUnion
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|naiveUnion
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Operations
operator|.
name|hasDeadStatesFromInitial
argument_list|(
name|naiveUnion
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|union
argument_list|,
name|naiveUnion
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|naiveUnion
specifier|private
specifier|static
name|Automaton
name|naiveUnion
parameter_list|(
name|List
argument_list|<
name|BytesRef
argument_list|>
name|strings
parameter_list|)
block|{
name|Automaton
index|[]
name|eachIndividual
init|=
operator|new
name|Automaton
index|[
name|strings
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|bref
range|:
name|strings
control|)
block|{
name|eachIndividual
index|[
name|i
operator|++
index|]
operator|=
name|Automata
operator|.
name|makeString
argument_list|(
name|bref
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Operations
operator|.
name|determinize
argument_list|(
name|Operations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|eachIndividual
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
return|;
block|}
comment|/** Test concatenation with empty language returns empty */
DECL|method|testEmptyLanguageConcatenate
specifier|public
name|void
name|testEmptyLanguageConcatenate
parameter_list|()
block|{
name|Automaton
name|a
init|=
name|Automata
operator|.
name|makeString
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Automaton
name|concat
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|Automata
operator|.
name|makeEmpty
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|isEmpty
argument_list|(
name|concat
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test optimization to concatenate() with empty String to an NFA */
DECL|method|testEmptySingletonNFAConcatenate
specifier|public
name|void
name|testEmptySingletonNFAConcatenate
parameter_list|()
block|{
name|Automaton
name|singleton
init|=
name|Automata
operator|.
name|makeString
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Automaton
name|expandedSingleton
init|=
name|singleton
decl_stmt|;
comment|// an NFA (two transitions for 't' from initial state)
name|Automaton
name|nfa
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"this"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"three"
argument_list|)
argument_list|)
decl_stmt|;
name|Automaton
name|concat1
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|expandedSingleton
argument_list|,
name|nfa
argument_list|)
decl_stmt|;
name|Automaton
name|concat2
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|singleton
argument_list|,
name|nfa
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|concat2
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|concat1
argument_list|,
literal|100
argument_list|)
argument_list|,
name|Operations
operator|.
name|determinize
argument_list|(
name|concat2
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|nfa
argument_list|,
literal|100
argument_list|)
argument_list|,
name|Operations
operator|.
name|determinize
argument_list|(
name|concat1
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|nfa
argument_list|,
literal|100
argument_list|)
argument_list|,
name|Operations
operator|.
name|determinize
argument_list|(
name|concat2
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetRandomAcceptedString
specifier|public
name|void
name|testGetRandomAcceptedString
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|int
name|ITER1
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ITER2
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
name|ITER1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RegExp
name|re
init|=
operator|new
name|RegExp
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
comment|//System.out.println("TEST i=" + i + " re=" + re);
specifier|final
name|Automaton
name|a
init|=
name|Operations
operator|.
name|determinize
argument_list|(
name|re
operator|.
name|toAutomaton
argument_list|()
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|Operations
operator|.
name|isEmpty
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStrings
name|rx
init|=
operator|new
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStrings
argument_list|(
name|a
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
name|ITER2
condition|;
name|j
operator|++
control|)
block|{
comment|//System.out.println("TEST: j=" + j);
name|int
index|[]
name|acc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|acc
operator|=
name|rx
operator|.
name|getRandomAcceptedString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s
init|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|acc
argument_list|,
literal|0
argument_list|,
name|acc
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//a.writeDot("adot");
name|assertTrue
argument_list|(
name|Operations
operator|.
name|run
argument_list|(
name|a
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"regexp: "
operator|+
name|re
argument_list|)
expr_stmt|;
if|if
condition|(
name|acc
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fail acc re="
operator|+
name|re
operator|+
literal|" count="
operator|+
name|acc
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|acc
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|acc
index|[
name|k
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
name|t
throw|;
block|}
block|}
block|}
block|}
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
name|Automaton
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
name|Operations
operator|.
name|isFinite
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the set of all accepted strings.    *    * This method exist just to ease testing.    * For production code directly use {@link FiniteStringsIterator} instead.    *    * @see FiniteStringsIterator    */
DECL|method|getFiniteStrings
specifier|public
specifier|static
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
return|return
name|getFiniteStrings
argument_list|(
operator|new
name|FiniteStringsIterator
argument_list|(
name|a
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the set of accepted strings, up to at most<code>limit</code> strings.    *    * This method exist just to ease testing.    * For production code directly use {@link LimitedFiniteStringsIterator} instead.    *    * @see LimitedFiniteStringsIterator    */
DECL|method|getFiniteStrings
specifier|public
specifier|static
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
return|return
name|getFiniteStrings
argument_list|(
operator|new
name|LimitedFiniteStringsIterator
argument_list|(
name|a
argument_list|,
name|limit
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get all finite strings of an iterator.    */
DECL|method|getFiniteStrings
specifier|private
specifier|static
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|FiniteStringsIterator
name|iterator
parameter_list|)
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
operator|new
name|HashSet
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
block|}
end_class

end_unit

