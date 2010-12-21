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
name|RamUsageEstimator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** Can next() and advance() through the terms in an FST   * @lucene.experimental */
end_comment

begin_class
DECL|class|IntsRefFSTEnum
specifier|public
class|class
name|IntsRefFSTEnum
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|T
argument_list|>
name|fst
decl_stmt|;
DECL|field|current
specifier|private
name|IntsRef
name|current
init|=
operator|new
name|IntsRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|arcs
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
name|arcs
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
literal|10
index|]
decl_stmt|;
comment|// outputs are cumulative
DECL|field|output
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|T
index|[]
name|output
init|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
literal|10
index|]
decl_stmt|;
DECL|field|lastFinal
specifier|private
name|boolean
name|lastFinal
decl_stmt|;
DECL|field|didEmpty
specifier|private
name|boolean
name|didEmpty
decl_stmt|;
DECL|field|NO_OUTPUT
specifier|private
specifier|final
name|T
name|NO_OUTPUT
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|InputOutput
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|InputOutput
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|InputOutput
specifier|public
specifier|static
class|class
name|InputOutput
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|input
specifier|public
name|IntsRef
name|input
decl_stmt|;
DECL|field|output
specifier|public
name|T
name|output
decl_stmt|;
block|}
DECL|method|IntsRefFSTEnum
specifier|public
name|IntsRefFSTEnum
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|)
block|{
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
name|result
operator|.
name|input
operator|=
name|current
expr_stmt|;
name|NO_OUTPUT
operator|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|lastFinal
operator|=
literal|false
expr_stmt|;
name|didEmpty
operator|=
literal|false
expr_stmt|;
name|current
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|output
operator|=
name|NO_OUTPUT
expr_stmt|;
block|}
comment|/** NOTE: target must be>= where we are already    *  positioned */
DECL|method|advance
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|advance
parameter_list|(
name|IntsRef
name|target
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|target
operator|.
name|compareTo
argument_list|(
name|current
argument_list|)
operator|>=
literal|0
assert|;
comment|//System.out.println("    advance len=" + target.length + " curlen=" + current.length);
comment|// special case empty string
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|target
operator|.
name|length
operator|==
literal|0
condition|)
block|{
specifier|final
name|T
name|output
init|=
name|fst
operator|.
name|getEmptyOutput
argument_list|()
decl_stmt|;
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|didEmpty
condition|)
block|{
name|current
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|lastFinal
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|output
operator|=
name|output
expr_stmt|;
name|didEmpty
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
block|{
return|return
name|next
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|fst
operator|.
name|noNodes
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|// TODO: possibly caller could/should provide common
comment|// prefix length?  ie this work may be redundant if
comment|// caller is in fact intersecting against its own
comment|// automaton
comment|// what prefix does target share w/ current
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|idx
operator|<
name|current
operator|.
name|length
operator|&&
name|idx
operator|<
name|target
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|ints
index|[
name|idx
index|]
operator|!=
name|target
operator|.
name|ints
index|[
name|target
operator|.
name|offset
operator|+
name|idx
index|]
condition|)
block|{
break|break;
block|}
name|idx
operator|++
expr_stmt|;
block|}
comment|//System.out.println("  shared " + idx);
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// new enum (no seek/next yet)
name|arc
operator|=
name|fst
operator|.
name|readFirstArc
argument_list|(
name|fst
operator|.
name|getStartNode
argument_list|()
argument_list|,
name|getArc
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("  new enum");
block|}
elseif|else
if|if
condition|(
name|idx
operator|<
name|current
operator|.
name|length
condition|)
block|{
comment|// roll back to shared point
name|lastFinal
operator|=
literal|false
expr_stmt|;
name|current
operator|.
name|length
operator|=
name|idx
expr_stmt|;
name|arc
operator|=
name|arcs
index|[
name|idx
index|]
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
if|if
condition|(
name|idx
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|next
argument_list|()
return|;
block|}
block|}
name|arc
operator|=
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|==
name|target
operator|.
name|length
condition|)
block|{
comment|// degenerate case -- seek to term we are already on
assert|assert
name|target
operator|.
name|equals
argument_list|(
name|current
argument_list|)
assert|;
return|return
name|result
return|;
block|}
else|else
block|{
comment|// current is a full prefix of target
if|if
condition|(
name|lastFinal
condition|)
block|{
name|arc
operator|=
name|fst
operator|.
name|readFirstArc
argument_list|(
name|arcs
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|target
argument_list|,
name|getArc
argument_list|(
name|current
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|next
argument_list|()
return|;
block|}
block|}
name|lastFinal
operator|=
literal|false
expr_stmt|;
assert|assert
name|arc
operator|==
name|arcs
index|[
name|current
operator|.
name|length
index|]
assert|;
name|int
name|targetLabel
init|=
name|target
operator|.
name|ints
index|[
name|target
operator|.
name|offset
operator|+
name|current
operator|.
name|length
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("    cycle len=" + current.length + " target=" + ((char) targetLabel) + " vs " + ((char) arc.label));
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|targetLabel
condition|)
block|{
name|grow
argument_list|()
expr_stmt|;
name|current
operator|.
name|ints
index|[
name|current
operator|.
name|length
index|]
operator|=
name|arc
operator|.
name|label
expr_stmt|;
name|appendOutput
argument_list|(
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
name|current
operator|.
name|length
operator|++
expr_stmt|;
name|grow
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|length
operator|==
name|target
operator|.
name|length
condition|)
block|{
name|result
operator|.
name|output
operator|=
name|output
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
comment|// target is exact match
if|if
condition|(
name|fst
operator|.
name|hasArcs
argument_list|(
name|arc
operator|.
name|target
argument_list|)
condition|)
block|{
comment|// target is also a proper prefix of other terms
name|lastFinal
operator|=
literal|true
expr_stmt|;
name|appendFinalOutput
argument_list|(
name|arc
operator|.
name|nextFinalOutput
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// target is not a match but is a prefix of
comment|// other terms
name|current
operator|.
name|length
operator|--
expr_stmt|;
name|push
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fst
operator|.
name|hasArcs
argument_list|(
name|arc
operator|.
name|target
argument_list|)
condition|)
block|{
comment|// we only match a prefix of the target
return|return
name|next
argument_list|()
return|;
block|}
else|else
block|{
name|targetLabel
operator|=
name|target
operator|.
name|ints
index|[
name|target
operator|.
name|offset
operator|+
name|current
operator|.
name|length
index|]
expr_stmt|;
name|arc
operator|=
name|fst
operator|.
name|readFirstArc
argument_list|(
name|arc
operator|.
name|target
argument_list|,
name|getArc
argument_list|(
name|current
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|label
operator|>
name|targetLabel
condition|)
block|{
comment|// we are now past the target
name|push
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|next
argument_list|()
return|;
block|}
else|else
block|{
name|arc
operator|=
name|fst
operator|.
name|readNextArc
argument_list|(
name|getArc
argument_list|(
name|current
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|current
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|current
parameter_list|()
block|{
return|return
name|result
return|;
block|}
DECL|method|next
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("  enum.next");
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
specifier|final
name|T
name|output
init|=
name|fst
operator|.
name|getEmptyOutput
argument_list|()
decl_stmt|;
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|didEmpty
condition|)
block|{
name|current
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|lastFinal
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|output
operator|=
name|output
expr_stmt|;
name|didEmpty
operator|=
literal|true
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
block|{
name|lastFinal
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fst
operator|.
name|noNodes
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|fst
operator|.
name|readFirstArc
argument_list|(
name|fst
operator|.
name|getStartNode
argument_list|()
argument_list|,
name|getArc
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|push
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastFinal
condition|)
block|{
name|lastFinal
operator|=
literal|false
expr_stmt|;
assert|assert
name|current
operator|.
name|length
operator|>
literal|0
assert|;
comment|// resume pushing
name|fst
operator|.
name|readFirstArc
argument_list|(
name|arcs
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|target
argument_list|,
name|getArc
argument_list|(
name|current
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|push
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("    pop/push");
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// enum done
return|return
literal|null
return|;
block|}
else|else
block|{
name|current
operator|.
name|length
operator|--
expr_stmt|;
name|fst
operator|.
name|readNextArc
argument_list|(
name|arcs
index|[
name|current
operator|.
name|length
index|]
argument_list|)
expr_stmt|;
name|push
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|grow
specifier|private
name|void
name|grow
parameter_list|()
block|{
specifier|final
name|int
name|l
init|=
name|current
operator|.
name|length
operator|+
literal|1
decl_stmt|;
name|current
operator|.
name|grow
argument_list|(
name|l
argument_list|)
expr_stmt|;
if|if
condition|(
name|arcs
operator|.
name|length
operator|<
name|l
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
name|newArcs
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|l
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
name|arcs
argument_list|,
literal|0
argument_list|,
name|newArcs
argument_list|,
literal|0
argument_list|,
name|arcs
operator|.
name|length
argument_list|)
expr_stmt|;
name|arcs
operator|=
name|newArcs
expr_stmt|;
block|}
if|if
condition|(
name|output
operator|.
name|length
operator|<
name|l
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|T
index|[]
name|newOutput
init|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|l
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
name|output
argument_list|,
literal|0
argument_list|,
name|newOutput
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|=
name|newOutput
expr_stmt|;
block|}
block|}
DECL|method|appendOutput
specifier|private
name|void
name|appendOutput
parameter_list|(
name|T
name|addedOutput
parameter_list|)
block|{
name|T
name|newOutput
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|newOutput
operator|=
name|addedOutput
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|addedOutput
operator|==
name|NO_OUTPUT
condition|)
block|{
name|output
index|[
name|current
operator|.
name|length
index|]
operator|=
name|output
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
return|return;
block|}
else|else
block|{
name|newOutput
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|addedOutput
argument_list|)
expr_stmt|;
block|}
name|output
index|[
name|current
operator|.
name|length
index|]
operator|=
name|newOutput
expr_stmt|;
block|}
DECL|method|appendFinalOutput
specifier|private
name|void
name|appendFinalOutput
parameter_list|(
name|T
name|addedOutput
parameter_list|)
block|{
if|if
condition|(
name|current
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|result
operator|.
name|output
operator|=
name|addedOutput
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|output
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|addedOutput
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|push
specifier|private
name|void
name|push
parameter_list|()
throws|throws
name|IOException
block|{
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|arcs
index|[
name|current
operator|.
name|length
index|]
decl_stmt|;
assert|assert
name|arc
operator|!=
literal|null
assert|;
while|while
condition|(
literal|true
condition|)
block|{
name|grow
argument_list|()
expr_stmt|;
name|current
operator|.
name|ints
index|[
name|current
operator|.
name|length
index|]
operator|=
name|arc
operator|.
name|label
expr_stmt|;
name|appendOutput
argument_list|(
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
comment|//System.out.println("    push: append label=" + ((char) arc.label) + " output=" + fst.outputs.outputToString(arc.output));
name|current
operator|.
name|length
operator|++
expr_stmt|;
name|grow
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|fst
operator|.
name|hasArcs
argument_list|(
name|arc
operator|.
name|target
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|appendFinalOutput
argument_list|(
name|arc
operator|.
name|nextFinalOutput
argument_list|)
expr_stmt|;
name|lastFinal
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|arc
operator|=
name|fst
operator|.
name|readFirstArc
argument_list|(
name|arc
operator|.
name|target
argument_list|,
name|getArc
argument_list|(
name|current
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|output
operator|=
name|output
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
block|}
DECL|method|pop
specifier|private
name|void
name|pop
parameter_list|()
block|{
while|while
condition|(
name|current
operator|.
name|length
operator|>
literal|0
operator|&&
name|arcs
index|[
name|current
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|isLast
argument_list|()
condition|)
block|{
name|current
operator|.
name|length
operator|--
expr_stmt|;
block|}
block|}
DECL|method|getArc
specifier|private
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|getArc
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|arcs
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
name|arcs
index|[
name|idx
index|]
operator|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|arcs
index|[
name|idx
index|]
return|;
block|}
block|}
end_class

end_unit

