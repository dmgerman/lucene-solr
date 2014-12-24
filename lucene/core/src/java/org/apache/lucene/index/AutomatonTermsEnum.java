begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BytesRefBuilder
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
name|StringHelper
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|ByteRunAutomaton
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
name|automaton
operator|.
name|CompiledAutomaton
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
name|automaton
operator|.
name|Transition
import|;
end_import

begin_comment
comment|/**  * A FilteredTermsEnum that enumerates terms based upon what is accepted by a  * DFA.  *<p>  * The algorithm is such:  *<ol>  *<li>As long as matches are successful, keep reading sequentially.  *<li>When a match fails, skip to the next string in lexicographic order that  * does not enter a reject state.  *</ol>  *<p>  * The algorithm does not attempt to actually skip to the next string that is  * completely accepted. This is not possible when the language accepted by the  * FSM is not finite (i.e. * operator).  *</p>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AutomatonTermsEnum
class|class
name|AutomatonTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
comment|// a tableized array-based form of the DFA
DECL|field|runAutomaton
specifier|private
specifier|final
name|ByteRunAutomaton
name|runAutomaton
decl_stmt|;
comment|// common suffix of the automaton
DECL|field|commonSuffixRef
specifier|private
specifier|final
name|BytesRef
name|commonSuffixRef
decl_stmt|;
comment|// true if the automaton accepts a finite language
DECL|field|finite
specifier|private
specifier|final
name|boolean
name|finite
decl_stmt|;
comment|// array of sorted transitions for each state, indexed by state number
DECL|field|automaton
specifier|private
specifier|final
name|Automaton
name|automaton
decl_stmt|;
comment|// for path tracking: each long records gen when we last
comment|// visited the state; we use gens to avoid having to clear
DECL|field|visited
specifier|private
specifier|final
name|long
index|[]
name|visited
decl_stmt|;
DECL|field|curGen
specifier|private
name|long
name|curGen
decl_stmt|;
comment|// the reference used for seeking forwards through the term dictionary
DECL|field|seekBytesRef
specifier|private
specifier|final
name|BytesRefBuilder
name|seekBytesRef
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
comment|// true if we are enumerating an infinite portion of the DFA.
comment|// in this case it is faster to drive the query based on the terms dictionary.
comment|// when this is true, linearUpperBound indicate the end of range
comment|// of terms where we should simply do sequential reads instead.
DECL|field|linear
specifier|private
name|boolean
name|linear
init|=
literal|false
decl_stmt|;
DECL|field|linearUpperBound
specifier|private
specifier|final
name|BytesRef
name|linearUpperBound
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|/**    * Construct an enumerator based upon an automaton, enumerating the specified    * field, working on a supplied TermsEnum    *<p>    * @lucene.experimental     *<p>    * @param compiled CompiledAutomaton    */
DECL|method|AutomatonTermsEnum
specifier|public
name|AutomatonTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|CompiledAutomaton
name|compiled
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
name|this
operator|.
name|finite
operator|=
name|compiled
operator|.
name|finite
expr_stmt|;
name|this
operator|.
name|runAutomaton
operator|=
name|compiled
operator|.
name|runAutomaton
expr_stmt|;
assert|assert
name|this
operator|.
name|runAutomaton
operator|!=
literal|null
assert|;
name|this
operator|.
name|commonSuffixRef
operator|=
name|compiled
operator|.
name|commonSuffixRef
expr_stmt|;
name|this
operator|.
name|automaton
operator|=
name|compiled
operator|.
name|automaton
expr_stmt|;
comment|// used for path tracking, where each bit is a numbered state.
name|visited
operator|=
operator|new
name|long
index|[
name|runAutomaton
operator|.
name|getSize
argument_list|()
index|]
expr_stmt|;
block|}
comment|/**    * Returns true if the term matches the automaton. Also stashes away the term    * to assist with smart enumeration.    */
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
specifier|final
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
name|commonSuffixRef
operator|==
literal|null
operator|||
name|StringHelper
operator|.
name|endsWith
argument_list|(
name|term
argument_list|,
name|commonSuffixRef
argument_list|)
condition|)
block|{
if|if
condition|(
name|runAutomaton
operator|.
name|run
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|,
name|term
operator|.
name|length
argument_list|)
condition|)
return|return
name|linear
condition|?
name|AcceptStatus
operator|.
name|YES
else|:
name|AcceptStatus
operator|.
name|YES_AND_SEEK
return|;
else|else
return|return
operator|(
name|linear
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|linearUpperBound
argument_list|)
operator|<
literal|0
operator|)
condition|?
name|AcceptStatus
operator|.
name|NO
else|:
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
block|}
else|else
block|{
return|return
operator|(
name|linear
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|linearUpperBound
argument_list|)
operator|<
literal|0
operator|)
condition|?
name|AcceptStatus
operator|.
name|NO
else|:
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextSeekTerm
specifier|protected
name|BytesRef
name|nextSeekTerm
parameter_list|(
specifier|final
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("ATE.nextSeekTerm term=" + term);
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
assert|assert
name|seekBytesRef
operator|.
name|length
argument_list|()
operator|==
literal|0
assert|;
comment|// return the empty term, as it's valid
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|seekBytesRef
operator|.
name|get
argument_list|()
return|;
block|}
block|}
else|else
block|{
name|seekBytesRef
operator|.
name|copyBytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|// seek to the next possible string;
if|if
condition|(
name|nextString
argument_list|()
condition|)
block|{
return|return
name|seekBytesRef
operator|.
name|get
argument_list|()
return|;
comment|// reposition
block|}
else|else
block|{
return|return
literal|null
return|;
comment|// no more possible strings can match
block|}
block|}
DECL|field|transition
specifier|private
name|Transition
name|transition
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
comment|/**    * Sets the enum to operate in linear fashion, as we have found    * a looping transition at position: we set an upper bound and     * act like a TermRangeQuery for this portion of the term space.    */
DECL|method|setLinear
specifier|private
name|void
name|setLinear
parameter_list|(
name|int
name|position
parameter_list|)
block|{
assert|assert
name|linear
operator|==
literal|false
assert|;
name|int
name|state
init|=
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
decl_stmt|;
assert|assert
name|state
operator|==
literal|0
assert|;
name|int
name|maxInterval
init|=
literal|0xff
decl_stmt|;
comment|//System.out.println("setLinear pos=" + position + " seekbytesRef=" + seekBytesRef);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|position
condition|;
name|i
operator|++
control|)
block|{
name|state
operator|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|state
argument_list|,
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|i
argument_list|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
assert|assert
name|state
operator|>=
literal|0
operator|:
literal|"state="
operator|+
name|state
assert|;
block|}
specifier|final
name|int
name|numTransitions
init|=
name|automaton
operator|.
name|getNumTransitions
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|automaton
operator|.
name|initTransition
argument_list|(
name|state
argument_list|,
name|transition
argument_list|)
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
name|numTransitions
condition|;
name|i
operator|++
control|)
block|{
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|transition
argument_list|)
expr_stmt|;
if|if
condition|(
name|transition
operator|.
name|min
operator|<=
operator|(
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|position
argument_list|)
operator|&
literal|0xff
operator|)
operator|&&
operator|(
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|position
argument_list|)
operator|&
literal|0xff
operator|)
operator|<=
name|transition
operator|.
name|max
condition|)
block|{
name|maxInterval
operator|=
name|transition
operator|.
name|max
expr_stmt|;
break|break;
block|}
block|}
comment|// 0xff terms don't get the optimization... not worth the trouble.
if|if
condition|(
name|maxInterval
operator|!=
literal|0xff
condition|)
name|maxInterval
operator|++
expr_stmt|;
name|int
name|length
init|=
name|position
operator|+
literal|1
decl_stmt|;
comment|/* position + maxTransition */
if|if
condition|(
name|linearUpperBound
operator|.
name|bytes
operator|.
name|length
operator|<
name|length
condition|)
name|linearUpperBound
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|seekBytesRef
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|linearUpperBound
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|linearUpperBound
operator|.
name|bytes
index|[
name|position
index|]
operator|=
operator|(
name|byte
operator|)
name|maxInterval
expr_stmt|;
name|linearUpperBound
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|linear
operator|=
literal|true
expr_stmt|;
block|}
DECL|field|savedStates
specifier|private
specifier|final
name|IntsRefBuilder
name|savedStates
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
comment|/**    * Increments the byte buffer to the next String in binary order after s that will not put    * the machine into a reject state. If such a string does not exist, returns    * false.    *     * The correctness of this method depends upon the automaton being deterministic,    * and having no transitions to dead states.    *     * @return true if more possible solutions exist for the DFA    */
DECL|method|nextString
specifier|private
name|boolean
name|nextString
parameter_list|()
block|{
name|int
name|state
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|savedStates
operator|.
name|grow
argument_list|(
name|seekBytesRef
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|savedStates
operator|.
name|setIntAt
argument_list|(
literal|0
argument_list|,
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|curGen
operator|++
expr_stmt|;
name|linear
operator|=
literal|false
expr_stmt|;
comment|// walk the automaton until a character is rejected.
for|for
control|(
name|state
operator|=
name|savedStates
operator|.
name|intAt
argument_list|(
name|pos
argument_list|)
init|;
name|pos
operator|<
name|seekBytesRef
operator|.
name|length
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|visited
index|[
name|state
index|]
operator|=
name|curGen
expr_stmt|;
name|int
name|nextState
init|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|state
argument_list|,
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|pos
argument_list|)
operator|&
literal|0xff
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextState
operator|==
operator|-
literal|1
condition|)
break|break;
name|savedStates
operator|.
name|setIntAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|,
name|nextState
argument_list|)
expr_stmt|;
comment|// we found a loop, record it for faster enumeration
if|if
condition|(
operator|!
name|finite
operator|&&
operator|!
name|linear
operator|&&
name|visited
index|[
name|nextState
index|]
operator|==
name|curGen
condition|)
block|{
name|setLinear
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|nextState
expr_stmt|;
block|}
comment|// take the useful portion, and the last non-reject state, and attempt to
comment|// append characters that will match.
if|if
condition|(
name|nextString
argument_list|(
name|state
argument_list|,
name|pos
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|/* no more solutions exist from this useful portion, backtrack */
if|if
condition|(
operator|(
name|pos
operator|=
name|backtrack
argument_list|(
name|pos
argument_list|)
operator|)
operator|<
literal|0
condition|)
comment|/* no more solutions at all */
return|return
literal|false
return|;
specifier|final
name|int
name|newState
init|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|savedStates
operator|.
name|intAt
argument_list|(
name|pos
argument_list|)
argument_list|,
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|pos
argument_list|)
operator|&
literal|0xff
argument_list|)
decl_stmt|;
if|if
condition|(
name|newState
operator|>=
literal|0
operator|&&
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|newState
argument_list|)
condition|)
comment|/* String is good to go as-is */
return|return
literal|true
return|;
comment|/* else advance further */
comment|// TODO: paranoia? if we backtrack thru an infinite DFA, the loop detection is important!
comment|// for now, restart from scratch for all infinite DFAs
if|if
condition|(
operator|!
name|finite
condition|)
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns the next String in lexicographic order that will not put    * the machine into a reject state.     *     * This method traverses the DFA from the given position in the String,    * starting at the given state.    *     * If this cannot satisfy the machine, returns false. This method will    * walk the minimal path, in lexicographic order, as long as possible.    *     * If this method returns false, then there might still be more solutions,    * it is necessary to backtrack to find out.    *     * @param state current non-reject state    * @param position useful portion of the string    * @return true if more possible solutions exist for the DFA from this    *         position    */
DECL|method|nextString
specifier|private
name|boolean
name|nextString
parameter_list|(
name|int
name|state
parameter_list|,
name|int
name|position
parameter_list|)
block|{
comment|/*       * the next lexicographic character must be greater than the existing      * character, if it exists.      */
name|int
name|c
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|position
operator|<
name|seekBytesRef
operator|.
name|length
argument_list|()
condition|)
block|{
name|c
operator|=
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|position
argument_list|)
operator|&
literal|0xff
expr_stmt|;
comment|// if the next byte is 0xff and is not part of the useful portion,
comment|// then by definition it puts us in a reject state, and therefore this
comment|// path is dead. there cannot be any higher transitions. backtrack.
if|if
condition|(
name|c
operator|++
operator|==
literal|0xff
condition|)
return|return
literal|false
return|;
block|}
name|seekBytesRef
operator|.
name|setLength
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|visited
index|[
name|state
index|]
operator|=
name|curGen
expr_stmt|;
specifier|final
name|int
name|numTransitions
init|=
name|automaton
operator|.
name|getNumTransitions
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|automaton
operator|.
name|initTransition
argument_list|(
name|state
argument_list|,
name|transition
argument_list|)
expr_stmt|;
comment|// find the minimal path (lexicographic order) that is>= c
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTransitions
condition|;
name|i
operator|++
control|)
block|{
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|transition
argument_list|)
expr_stmt|;
if|if
condition|(
name|transition
operator|.
name|max
operator|>=
name|c
condition|)
block|{
name|int
name|nextChar
init|=
name|Math
operator|.
name|max
argument_list|(
name|c
argument_list|,
name|transition
operator|.
name|min
argument_list|)
decl_stmt|;
comment|// append either the next sequential char, or the minimum transition
name|seekBytesRef
operator|.
name|grow
argument_list|(
name|seekBytesRef
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|seekBytesRef
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|nextChar
argument_list|)
expr_stmt|;
name|state
operator|=
name|transition
operator|.
name|dest
expr_stmt|;
comment|/*           * as long as is possible, continue down the minimal path in          * lexicographic order. if a loop or accept state is encountered, stop.          */
while|while
condition|(
name|visited
index|[
name|state
index|]
operator|!=
name|curGen
operator|&&
operator|!
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|visited
index|[
name|state
index|]
operator|=
name|curGen
expr_stmt|;
comment|/*             * Note: we work with a DFA with no transitions to dead states.            * so the below is ok, if it is not an accept state,            * then there MUST be at least one transition.            */
name|automaton
operator|.
name|initTransition
argument_list|(
name|state
argument_list|,
name|transition
argument_list|)
expr_stmt|;
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|transition
argument_list|)
expr_stmt|;
name|state
operator|=
name|transition
operator|.
name|dest
expr_stmt|;
comment|// append the minimum transition
name|seekBytesRef
operator|.
name|grow
argument_list|(
name|seekBytesRef
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|seekBytesRef
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|transition
operator|.
name|min
argument_list|)
expr_stmt|;
comment|// we found a loop, record it for faster enumeration
if|if
condition|(
operator|!
name|finite
operator|&&
operator|!
name|linear
operator|&&
name|visited
index|[
name|state
index|]
operator|==
name|curGen
condition|)
block|{
name|setLinear
argument_list|(
name|seekBytesRef
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Attempts to backtrack thru the string after encountering a dead end    * at some given position. Returns false if no more possible strings     * can match.    *     * @param position current position in the input String    * @return {@code position>= 0} if more possible solutions exist for the DFA    */
DECL|method|backtrack
specifier|private
name|int
name|backtrack
parameter_list|(
name|int
name|position
parameter_list|)
block|{
while|while
condition|(
name|position
operator|--
operator|>
literal|0
condition|)
block|{
name|int
name|nextChar
init|=
name|seekBytesRef
operator|.
name|byteAt
argument_list|(
name|position
argument_list|)
operator|&
literal|0xff
decl_stmt|;
comment|// if a character is 0xff it's a dead-end too,
comment|// because there is no higher character in binary sort order.
if|if
condition|(
name|nextChar
operator|++
operator|!=
literal|0xff
condition|)
block|{
name|seekBytesRef
operator|.
name|setByteAt
argument_list|(
name|position
argument_list|,
operator|(
name|byte
operator|)
name|nextChar
argument_list|)
expr_stmt|;
name|seekBytesRef
operator|.
name|setLength
argument_list|(
name|position
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|position
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
comment|/* all solutions exhausted */
block|}
block|}
end_class

end_unit

