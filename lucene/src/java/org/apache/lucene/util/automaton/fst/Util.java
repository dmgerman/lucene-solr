begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton.fst
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
operator|.
name|fst
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

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

begin_comment
comment|/** Static helper methods */
end_comment

begin_class
DECL|class|Util
specifier|public
specifier|final
class|class
name|Util
block|{
DECL|method|Util
specifier|private
name|Util
parameter_list|()
block|{   }
comment|/** Looks up the output for this input, or null if the    *  input is not accepted. FST must be    *  INPUT_TYPE.BYTE4. */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|,
name|IntsRef
name|input
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|inputType
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
assert|;
comment|// TODO: would be nice not to alloc this on every lookup
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|fst
operator|.
name|getFirstArc
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Accumulate output as we go
specifier|final
name|T
name|NO_OUTPUT
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|T
name|output
init|=
name|NO_OUTPUT
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
name|input
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|input
operator|.
name|ints
index|[
name|input
operator|.
name|offset
operator|+
name|i
index|]
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
name|output
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|FST
operator|.
name|END_LABEL
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
return|return
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|output
return|;
block|}
block|}
comment|/** Logically casts input to UTF32 ints then looks up the output    *  or null if the input is not accepted.  FST must be    *  INPUT_TYPE.BYTE4.  */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|,
name|char
index|[]
name|input
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|inputType
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
assert|;
comment|// TODO: would be nice not to alloc this on every lookup
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|fst
operator|.
name|getFirstArc
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|charIdx
init|=
name|offset
decl_stmt|;
specifier|final
name|int
name|charLimit
init|=
name|offset
operator|+
name|length
decl_stmt|;
comment|// Accumulate output as we go
specifier|final
name|T
name|NO_OUTPUT
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|T
name|output
init|=
name|NO_OUTPUT
decl_stmt|;
while|while
condition|(
name|charIdx
operator|<
name|charLimit
condition|)
block|{
specifier|final
name|int
name|utf32
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|input
argument_list|,
name|charIdx
argument_list|)
decl_stmt|;
name|charIdx
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|utf32
argument_list|)
expr_stmt|;
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|utf32
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
name|output
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|FST
operator|.
name|END_LABEL
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
return|return
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|output
return|;
block|}
block|}
comment|/** Logically casts input to UTF32 ints then looks up the output    *  or null if the input is not accepted.  FST must be    *  INPUT_TYPE.BYTE4.  */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|,
name|CharSequence
name|input
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|inputType
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
assert|;
comment|// TODO: would be nice not to alloc this on every lookup
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|fst
operator|.
name|getFirstArc
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|charIdx
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|charLimit
init|=
name|input
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// Accumulate output as we go
specifier|final
name|T
name|NO_OUTPUT
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|T
name|output
init|=
name|NO_OUTPUT
decl_stmt|;
while|while
condition|(
name|charIdx
operator|<
name|charLimit
condition|)
block|{
specifier|final
name|int
name|utf32
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|input
argument_list|,
name|charIdx
argument_list|)
decl_stmt|;
name|charIdx
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|utf32
argument_list|)
expr_stmt|;
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|utf32
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
name|output
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|FST
operator|.
name|END_LABEL
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
return|return
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|output
return|;
block|}
block|}
comment|/** Looks up the output for this input, or null if the    *  input is not accepted */
DECL|method|get
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|,
name|BytesRef
name|input
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|inputType
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
assert|;
comment|// TODO: would be nice not to alloc this on every lookup
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|fst
operator|.
name|getFirstArc
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Accumulate output as we go
specifier|final
name|T
name|NO_OUTPUT
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|T
name|output
init|=
name|NO_OUTPUT
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
name|input
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|input
operator|.
name|bytes
index|[
name|i
operator|+
name|input
operator|.
name|offset
index|]
operator|&
literal|0xFF
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
name|output
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|FST
operator|.
name|END_LABEL
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
return|return
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
argument_list|,
name|arc
operator|.
name|output
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|output
return|;
block|}
block|}
comment|/**    * Dumps an {@link FST} to a GraphViz's<code>dot</code> language description    * for visualization. Example of use:    *     *<pre>    * PrintStream ps = new PrintStream(&quot;out.dot&quot;);    * fst.toDot(ps);    * ps.close();    *</pre>    *     * and then, from command line:    *     *<pre>    * dot -Tpng -o out.png out.dot    *</pre>    *     *<p>    * Note: larger FSTs (a few thousand nodes) won't even render, don't bother.    *     * @param sameRank    *          If<code>true</code>, the resulting<code>dot</code> file will try    *          to order states in layers of breadth-first traversal. This may    *          mess up arcs, but makes the output FST's structure a bit clearer.    *     * @param labelStates    *          If<code>true</code> states will have labels equal to their offsets in their    *          binary format. Expands the graph considerably.     *     * @see "http://www.graphviz.org/"    */
DECL|method|toDot
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|toDot
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|,
name|Writer
name|out
parameter_list|,
name|boolean
name|sameRank
parameter_list|,
name|boolean
name|labelStates
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This is the start arc in the automaton (from the epsilon state to the first state
comment|// with outgoing transitions.
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|startArc
init|=
name|fst
operator|.
name|getFirstArc
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// A queue of transitions to consider for the next level.
specifier|final
name|List
argument_list|<
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|>
name|thisLevelQueue
init|=
operator|new
name|ArrayList
argument_list|<
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// A queue of transitions to consider when processing the next level.
specifier|final
name|List
argument_list|<
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|>
name|nextLevelQueue
init|=
operator|new
name|ArrayList
argument_list|<
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|nextLevelQueue
operator|.
name|add
argument_list|(
name|startArc
argument_list|)
expr_stmt|;
comment|// A list of states on the same level (for ranking).
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|sameLevelStates
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
comment|// A bitset of already seen states (target offset).
specifier|final
name|BitSet
name|seen
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|seen
operator|.
name|set
argument_list|(
name|startArc
operator|.
name|target
argument_list|)
expr_stmt|;
comment|// Shape for states.
specifier|final
name|String
name|stateShape
init|=
literal|"circle"
decl_stmt|;
comment|// Emit DOT prologue.
name|out
operator|.
name|write
argument_list|(
literal|"digraph FST {\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"  rankdir = LR; splines=true; concentrate=true; ordering=out; ranksep=2.5; \n"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|labelStates
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"  node [shape=circle, width=.2, height=.2, style=filled]\n"
argument_list|)
expr_stmt|;
block|}
name|emitDotState
argument_list|(
name|out
argument_list|,
literal|"initial"
argument_list|,
literal|"point"
argument_list|,
literal|"white"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|emitDotState
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|startArc
operator|.
name|target
argument_list|)
argument_list|,
name|stateShape
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"  initial -> "
operator|+
name|startArc
operator|.
name|target
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
specifier|final
name|T
name|NO_OUTPUT
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|int
name|level
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|nextLevelQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we could double buffer here, but it doesn't matter probably.
name|thisLevelQueue
operator|.
name|addAll
argument_list|(
name|nextLevelQueue
argument_list|)
expr_stmt|;
name|nextLevelQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|level
operator|++
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"\n  // Transitions and states at level: "
operator|+
name|level
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|thisLevelQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|thisLevelQueue
operator|.
name|remove
argument_list|(
name|thisLevelQueue
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|fst
operator|.
name|targetHasArcs
argument_list|(
name|arc
argument_list|)
condition|)
block|{
comment|// scan all arcs
specifier|final
name|int
name|node
init|=
name|arc
operator|.
name|target
decl_stmt|;
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// Emit the unseen state and add it to the queue for the next level.
if|if
condition|(
name|arc
operator|.
name|target
operator|>=
literal|0
operator|&&
operator|!
name|seen
operator|.
name|get
argument_list|(
name|arc
operator|.
name|target
argument_list|)
condition|)
block|{
name|emitDotState
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|arc
operator|.
name|target
argument_list|)
argument_list|,
name|stateShape
argument_list|,
literal|null
argument_list|,
name|labelStates
condition|?
name|Integer
operator|.
name|toString
argument_list|(
name|arc
operator|.
name|target
argument_list|)
else|:
literal|""
argument_list|)
expr_stmt|;
name|seen
operator|.
name|set
argument_list|(
name|arc
operator|.
name|target
argument_list|)
expr_stmt|;
name|nextLevelQueue
operator|.
name|add
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|arc
argument_list|)
argument_list|)
expr_stmt|;
name|sameLevelStates
operator|.
name|add
argument_list|(
name|arc
operator|.
name|target
argument_list|)
expr_stmt|;
block|}
name|String
name|outs
decl_stmt|;
if|if
condition|(
name|arc
operator|.
name|output
operator|!=
name|NO_OUTPUT
condition|)
block|{
name|outs
operator|=
literal|"/"
operator|+
name|fst
operator|.
name|outputs
operator|.
name|outputToString
argument_list|(
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outs
operator|=
literal|""
expr_stmt|;
block|}
specifier|final
name|String
name|cl
decl_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
name|cl
operator|=
literal|"~"
expr_stmt|;
block|}
else|else
block|{
name|cl
operator|=
name|printableLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"  "
operator|+
name|node
operator|+
literal|" -> "
operator|+
name|arc
operator|.
name|target
operator|+
literal|" [label=\""
operator|+
name|cl
operator|+
name|outs
operator|+
literal|"\"]\n"
argument_list|)
expr_stmt|;
comment|// Break the loop if we're on the last arc of this state.
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
break|break;
block|}
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Emit state ranking information.
if|if
condition|(
name|sameRank
operator|&&
name|sameLevelStates
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"  {rank=same; "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|state
range|:
name|sameLevelStates
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|state
operator|+
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|" }\n"
argument_list|)
expr_stmt|;
block|}
name|sameLevelStates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Emit terminating state (always there anyway).
name|out
operator|.
name|write
argument_list|(
literal|"  -1 [style=filled, color=black, shape=circle, label=\"\"]\n\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"  {rank=sink; -1 } "
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Emit a single state in the<code>dot</code> language.     */
DECL|method|emitDotState
specifier|private
specifier|static
name|void
name|emitDotState
parameter_list|(
name|Writer
name|out
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|shape
parameter_list|,
name|String
name|color
parameter_list|,
name|String
name|label
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"  "
operator|+
name|name
operator|+
literal|" ["
operator|+
operator|(
name|shape
operator|!=
literal|null
condition|?
literal|"shape="
operator|+
name|shape
else|:
literal|""
operator|)
operator|+
literal|" "
operator|+
operator|(
name|color
operator|!=
literal|null
condition|?
literal|"color="
operator|+
name|color
else|:
literal|""
operator|)
operator|+
literal|" "
operator|+
operator|(
name|label
operator|!=
literal|null
condition|?
literal|"label=\""
operator|+
name|label
operator|+
literal|"\""
else|:
literal|"label=\"\""
operator|)
operator|+
literal|" "
operator|+
literal|"]\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensures an arc's label is indeed printable (dot uses US-ASCII).     */
DECL|method|printableLabel
specifier|private
specifier|static
name|String
name|printableLabel
parameter_list|(
name|int
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|>=
literal|0x20
operator|&&
name|label
operator|<=
literal|0x7d
condition|)
block|{
return|return
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|label
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|"0x"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|label
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

