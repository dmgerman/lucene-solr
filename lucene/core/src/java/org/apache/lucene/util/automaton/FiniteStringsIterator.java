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
name|BitSet
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
name|ArrayUtil
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * Iterates all accepted strings.  *  *<p>If the {@link Automaton} has cycles then this iterator may throw an {@code  * IllegalArgumentException}, but this is not guaranteed!  *  *<p>Be aware that the iteration order is implementation dependent  * and may change across releases.  *  *<p>If the automaton is not determinized then it's possible this iterator  * will return duplicates.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FiniteStringsIterator
specifier|public
class|class
name|FiniteStringsIterator
block|{
comment|/**    * Empty string.    */
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|IntsRef
name|EMPTY
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
comment|/**    * Automaton to create finite string from.    */
DECL|field|a
specifier|private
specifier|final
name|Automaton
name|a
decl_stmt|;
comment|/**    * The state where each path should stop or -1 if only accepted states should be final.    */
DECL|field|endState
specifier|private
specifier|final
name|int
name|endState
decl_stmt|;
comment|/**    * Tracks which states are in the current path, for cycle detection.    */
DECL|field|pathStates
specifier|private
specifier|final
name|BitSet
name|pathStates
decl_stmt|;
comment|/**    * Builder for current finite string.    */
DECL|field|string
specifier|private
specifier|final
name|IntsRefBuilder
name|string
decl_stmt|;
comment|/**    * Stack to hold our current state in the recursion/iteration.    */
DECL|field|nodes
specifier|private
name|PathNode
index|[]
name|nodes
decl_stmt|;
comment|/**    * Emit empty string?.    */
DECL|field|emitEmptyString
specifier|private
name|boolean
name|emitEmptyString
decl_stmt|;
comment|/**    * Constructor.    *    * @param a Automaton to create finite string from.    */
DECL|method|FiniteStringsIterator
specifier|public
name|FiniteStringsIterator
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|this
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    *    * @param a Automaton to create finite string from.    * @param startState The starting state for each path.    * @param endState The state where each path should stop or -1 if only accepted states should be final.    */
DECL|method|FiniteStringsIterator
specifier|public
name|FiniteStringsIterator
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|int
name|startState
parameter_list|,
name|int
name|endState
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|endState
operator|=
name|endState
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
operator|new
name|PathNode
index|[
literal|16
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|end
init|=
name|nodes
operator|.
name|length
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
operator|new
name|PathNode
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|string
operator|=
operator|new
name|IntsRefBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|pathStates
operator|=
operator|new
name|BitSet
argument_list|(
name|a
operator|.
name|getNumStates
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|string
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|emitEmptyString
operator|=
name|a
operator|.
name|isAccept
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Start iteration with node startState.
if|if
condition|(
name|a
operator|.
name|getNumTransitions
argument_list|(
name|startState
argument_list|)
operator|>
literal|0
condition|)
block|{
name|pathStates
operator|.
name|set
argument_list|(
name|startState
argument_list|)
expr_stmt|;
name|nodes
index|[
literal|0
index|]
operator|.
name|resetState
argument_list|(
name|a
argument_list|,
name|startState
argument_list|)
expr_stmt|;
name|string
operator|.
name|append
argument_list|(
name|startState
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Generate next finite string.    * The return value is just valid until the next call of this method!    *    * @return Finite string or null, if no more finite strings are available.    */
DECL|method|next
specifier|public
name|IntsRef
name|next
parameter_list|()
block|{
comment|// Special case the empty string, as usual:
if|if
condition|(
name|emitEmptyString
condition|)
block|{
name|emitEmptyString
operator|=
literal|false
expr_stmt|;
return|return
name|EMPTY
return|;
block|}
for|for
control|(
name|int
name|depth
init|=
name|string
operator|.
name|length
argument_list|()
init|;
name|depth
operator|>
literal|0
condition|;
control|)
block|{
name|PathNode
name|node
init|=
name|nodes
index|[
name|depth
operator|-
literal|1
index|]
decl_stmt|;
comment|// Get next label leaving the current node:
name|int
name|label
init|=
name|node
operator|.
name|nextLabel
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|label
operator|!=
operator|-
literal|1
condition|)
block|{
name|string
operator|.
name|setIntAt
argument_list|(
name|depth
operator|-
literal|1
argument_list|,
name|label
argument_list|)
expr_stmt|;
name|int
name|to
init|=
name|node
operator|.
name|to
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|getNumTransitions
argument_list|(
name|to
argument_list|)
operator|!=
literal|0
operator|&&
name|to
operator|!=
name|endState
condition|)
block|{
comment|// Now recurse: the destination of this transition has outgoing transitions:
if|if
condition|(
name|pathStates
operator|.
name|get
argument_list|(
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"automaton has cycles"
argument_list|)
throw|;
block|}
name|pathStates
operator|.
name|set
argument_list|(
name|to
argument_list|)
expr_stmt|;
comment|// Push node onto stack:
name|growStack
argument_list|(
name|depth
argument_list|)
expr_stmt|;
name|nodes
index|[
name|depth
index|]
operator|.
name|resetState
argument_list|(
name|a
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|depth
operator|++
expr_stmt|;
name|string
operator|.
name|setLength
argument_list|(
name|depth
argument_list|)
expr_stmt|;
name|string
operator|.
name|grow
argument_list|(
name|depth
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|endState
operator|==
name|to
operator|||
name|a
operator|.
name|isAccept
argument_list|(
name|to
argument_list|)
condition|)
block|{
comment|// This transition leads to an accept state, so we save the current string:
return|return
name|string
operator|.
name|get
argument_list|()
return|;
block|}
block|}
else|else
block|{
comment|// No more transitions leaving this state, pop/return back to previous state:
name|int
name|state
init|=
name|node
operator|.
name|state
decl_stmt|;
assert|assert
name|pathStates
operator|.
name|get
argument_list|(
name|state
argument_list|)
assert|;
name|pathStates
operator|.
name|clear
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|depth
operator|--
expr_stmt|;
name|string
operator|.
name|setLength
argument_list|(
name|depth
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
comment|// This transition leads to an accept state, so we save the current string:
return|return
name|string
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
comment|// Finished iteration.
return|return
literal|null
return|;
block|}
comment|/**    * Grow path stack, if required.    */
DECL|method|growStack
specifier|private
name|void
name|growStack
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
name|depth
condition|)
block|{
name|PathNode
index|[]
name|newNodes
init|=
operator|new
name|PathNode
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|nodes
operator|.
name|length
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodes
argument_list|,
literal|0
argument_list|,
name|newNodes
argument_list|,
literal|0
argument_list|,
name|nodes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|depth
init|,
name|end
init|=
name|newNodes
operator|.
name|length
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|newNodes
index|[
name|i
index|]
operator|=
operator|new
name|PathNode
argument_list|()
expr_stmt|;
block|}
name|nodes
operator|=
name|newNodes
expr_stmt|;
block|}
block|}
comment|/**    * Nodes for path stack.    */
DECL|class|PathNode
specifier|private
specifier|static
class|class
name|PathNode
block|{
comment|/** Which state the path node ends on, whose      *  transitions we are enumerating. */
DECL|field|state
specifier|public
name|int
name|state
decl_stmt|;
comment|/** Which state the current transition leads to. */
DECL|field|to
specifier|public
name|int
name|to
decl_stmt|;
comment|/** Which transition we are on. */
DECL|field|transition
specifier|public
name|int
name|transition
decl_stmt|;
comment|/** Which label we are on, in the min-max range of the      *  current Transition */
DECL|field|label
specifier|public
name|int
name|label
decl_stmt|;
DECL|field|t
specifier|private
specifier|final
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
DECL|method|resetState
specifier|public
name|void
name|resetState
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|int
name|state
parameter_list|)
block|{
assert|assert
name|a
operator|.
name|getNumTransitions
argument_list|(
name|state
argument_list|)
operator|!=
literal|0
assert|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|transition
operator|=
literal|0
expr_stmt|;
name|a
operator|.
name|getTransition
argument_list|(
name|state
argument_list|,
literal|0
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|label
operator|=
name|t
operator|.
name|min
expr_stmt|;
name|to
operator|=
name|t
operator|.
name|dest
expr_stmt|;
block|}
comment|/** Returns next label of current transition, or      *  advances to next transition and returns its first      *  label, if current one is exhausted.  If there are      *  no more transitions, returns -1. */
DECL|method|nextLabel
specifier|public
name|int
name|nextLabel
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|label
operator|>
name|t
operator|.
name|max
condition|)
block|{
comment|// We've exhaused the current transition's labels;
comment|// move to next transitions:
name|transition
operator|++
expr_stmt|;
if|if
condition|(
name|transition
operator|>=
name|a
operator|.
name|getNumTransitions
argument_list|(
name|state
argument_list|)
condition|)
block|{
comment|// We're done iterating transitions leaving this state
name|label
operator|=
operator|-
literal|1
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|a
operator|.
name|getTransition
argument_list|(
name|state
argument_list|,
name|transition
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|label
operator|=
name|t
operator|.
name|min
expr_stmt|;
name|to
operator|=
name|t
operator|.
name|dest
expr_stmt|;
block|}
return|return
name|label
operator|++
return|;
block|}
block|}
block|}
end_class

end_unit

