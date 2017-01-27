begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|HashMap
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
name|Map
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|IndexReaderContext
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
name|index
operator|.
name|LeafReaderContext
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
name|index
operator|.
name|PostingsEnum
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
name|index
operator|.
name|ReaderUtil
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermContext
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
name|index
operator|.
name|TermState
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
name|index
operator|.
name|TermsEnum
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|search
operator|.
name|spans
operator|.
name|SpanNearQuery
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
name|Operations
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
comment|// TODO
end_comment

begin_comment
comment|//    - compare perf to PhraseQuery exact and sloppy
end_comment

begin_comment
comment|//    - optimize: find terms that are in fact MUST (because all paths
end_comment

begin_comment
comment|//      through the A include that term)
end_comment

begin_comment
comment|//    - if we ever store posLength in the index, it would be easy[ish]
end_comment

begin_comment
comment|//      to take it into account here
end_comment

begin_comment
comment|/** A proximity query that lets you express an automaton, whose  *  transitions are terms, to match documents.  This is a generalization  *  of other proximity queries like  {@link PhraseQuery}, {@link  *  MultiPhraseQuery} and {@link SpanNearQuery}.  It is likely  *  slow, since it visits any document having any of the terms (i.e. it  *  acts like a disjunction, not a conjunction like {@link  *  PhraseQuery}), and then it must merge-sort all positions within each  *  document to test whether/how many times the automaton matches.  *  *<p>After creating the query, use {@link #createState}, {@link  *  #setAccept}, {@link #addTransition} and {@link #addAnyTransition} to  *  build up the automaton.  Once you are done, call {@link #finish} and  *  then execute the query.  *  *<p>This code is very new and likely has exciting bugs!  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|TermAutomatonQuery
specifier|public
class|class
name|TermAutomatonQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|builder
specifier|private
specifier|final
name|Automaton
operator|.
name|Builder
name|builder
decl_stmt|;
DECL|field|det
name|Automaton
name|det
decl_stmt|;
DECL|field|termToID
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|termToID
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|idToTerm
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BytesRef
argument_list|>
name|idToTerm
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|anyTermID
specifier|private
name|int
name|anyTermID
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|TermAutomatonQuery
specifier|public
name|TermAutomatonQuery
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|builder
operator|=
operator|new
name|Automaton
operator|.
name|Builder
argument_list|()
expr_stmt|;
block|}
comment|/** Returns a new state; state 0 is always the initial state. */
DECL|method|createState
specifier|public
name|int
name|createState
parameter_list|()
block|{
return|return
name|builder
operator|.
name|createState
argument_list|()
return|;
block|}
comment|/** Marks the specified state as accept or not. */
DECL|method|setAccept
specifier|public
name|void
name|setAccept
parameter_list|(
name|int
name|state
parameter_list|,
name|boolean
name|accept
parameter_list|)
block|{
name|builder
operator|.
name|setAccept
argument_list|(
name|state
argument_list|,
name|accept
argument_list|)
expr_stmt|;
block|}
comment|/** Adds a transition to the automaton. */
DECL|method|addTransition
specifier|public
name|void
name|addTransition
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|dest
parameter_list|,
name|String
name|term
parameter_list|)
block|{
name|addTransition
argument_list|(
name|source
argument_list|,
name|dest
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Adds a transition to the automaton. */
DECL|method|addTransition
specifier|public
name|void
name|addTransition
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|dest
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"term should not be null"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|addTransition
argument_list|(
name|source
argument_list|,
name|dest
argument_list|,
name|getTermID
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Adds a transition matching any term. */
DECL|method|addAnyTransition
specifier|public
name|void
name|addAnyTransition
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|dest
parameter_list|)
block|{
name|builder
operator|.
name|addTransition
argument_list|(
name|source
argument_list|,
name|dest
argument_list|,
name|getTermID
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Call this once you are done adding states/transitions. */
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
block|{
name|finish
argument_list|(
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Call this once you are done adding states/transitions.    * @param maxDeterminizedStates Maximum number of states created when    *   determinizing the automaton.  Higher numbers allow this operation to    *   consume more memory but allow more complex automatons.    */
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|Automaton
name|automaton
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
comment|// System.out.println("before det:\n" + automaton.toDot());
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
comment|// TODO: should we add "eps back to initial node" for all states,
comment|// and det that?  then we don't need to revisit initial node at
comment|// every position?  but automaton could blow up?  And, this makes it
comment|// harder to skip useless positions at search time?
if|if
condition|(
name|anyTermID
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Make sure there are no leading or trailing ANY:
name|int
name|count
init|=
name|automaton
operator|.
name|initTransition
argument_list|(
literal|0
argument_list|,
name|t
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|anyTermID
operator|>=
name|t
operator|.
name|min
operator|&&
name|anyTermID
operator|<=
name|t
operator|.
name|max
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"automaton cannot lead with an ANY transition"
argument_list|)
throw|;
block|}
block|}
name|int
name|numStates
init|=
name|automaton
operator|.
name|getNumStates
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
name|numStates
condition|;
name|i
operator|++
control|)
block|{
name|count
operator|=
name|automaton
operator|.
name|initTransition
argument_list|(
name|i
argument_list|,
name|t
argument_list|)
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
name|count
condition|;
name|j
operator|++
control|)
block|{
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|automaton
operator|.
name|isAccept
argument_list|(
name|t
operator|.
name|dest
argument_list|)
operator|&&
name|anyTermID
operator|>=
name|t
operator|.
name|min
operator|&&
name|anyTermID
operator|<=
name|t
operator|.
name|max
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"automaton cannot end with an ANY transition"
argument_list|)
throw|;
block|}
block|}
block|}
name|int
name|termCount
init|=
name|termToID
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// We have to carefully translate these transitions so automaton
comment|// realizes they also match all other terms:
name|Automaton
name|newAutomaton
init|=
operator|new
name|Automaton
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
name|numStates
condition|;
name|i
operator|++
control|)
block|{
name|newAutomaton
operator|.
name|createState
argument_list|()
expr_stmt|;
name|newAutomaton
operator|.
name|setAccept
argument_list|(
name|i
argument_list|,
name|automaton
operator|.
name|isAccept
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numStates
condition|;
name|i
operator|++
control|)
block|{
name|count
operator|=
name|automaton
operator|.
name|initTransition
argument_list|(
name|i
argument_list|,
name|t
argument_list|)
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
name|count
condition|;
name|j
operator|++
control|)
block|{
name|automaton
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|int
name|min
decl_stmt|,
name|max
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|min
operator|<=
name|anyTermID
operator|&&
name|anyTermID
operator|<=
name|t
operator|.
name|max
condition|)
block|{
comment|// Match any term
name|min
operator|=
literal|0
expr_stmt|;
name|max
operator|=
name|termCount
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
name|t
operator|.
name|min
expr_stmt|;
name|max
operator|=
name|t
operator|.
name|max
expr_stmt|;
block|}
name|newAutomaton
operator|.
name|addTransition
argument_list|(
name|t
operator|.
name|source
argument_list|,
name|t
operator|.
name|dest
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
name|newAutomaton
operator|.
name|finishState
argument_list|()
expr_stmt|;
name|automaton
operator|=
name|newAutomaton
expr_stmt|;
block|}
name|det
operator|=
name|Operations
operator|.
name|removeDeadStates
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|automaton
argument_list|,
name|maxDeterminizedStates
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|det
operator|.
name|isAccept
argument_list|(
literal|0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot accept the empty string"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReaderContext
name|context
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|TermContext
argument_list|>
name|termStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|termToID
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|ent
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|termStates
operator|.
name|put
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|,
name|TermContext
operator|.
name|build
argument_list|(
name|context
argument_list|,
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|TermAutomatonWeight
argument_list|(
name|det
argument_list|,
name|searcher
argument_list|,
name|termStates
argument_list|,
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|// TODO: what really am I supposed to do with the incoming field...
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
literal|"TermAutomatonQuery(field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|det
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" numStates="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|det
operator|.
name|getNumStates
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getTermID
specifier|private
name|int
name|getTermID
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|Integer
name|id
init|=
name|termToID
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|id
operator|=
name|termToID
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
name|term
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|termToID
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|idToTerm
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|anyTermID
operator|=
name|id
expr_stmt|;
block|}
block|}
return|return
name|id
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkFinished
specifier|private
specifier|static
name|boolean
name|checkFinished
parameter_list|(
name|TermAutomatonQuery
name|q
parameter_list|)
block|{
if|if
condition|(
name|q
operator|.
name|det
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Call finish first on: "
operator|+
name|q
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|TermAutomatonQuery
name|other
parameter_list|)
block|{
return|return
name|checkFinished
argument_list|(
name|this
argument_list|)
operator|&&
name|checkFinished
argument_list|(
name|other
argument_list|)
operator|&&
name|other
operator|==
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|checkFinished
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// LUCENE-7295: this used to be very awkward toDot() call; it is safer to assume
comment|// that no two instances are equivalent instead (until somebody finds a better way to check
comment|// on automaton equivalence quickly).
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Returns the dot (graphviz) representation of this automaton.    *  This is extremely useful for visualizing the automaton. */
DECL|method|toDot
specifier|public
name|String
name|toDot
parameter_list|()
block|{
comment|// TODO: refactor& share with Automaton.toDot!
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"digraph Automaton {\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"  rankdir = LR\n"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numStates
init|=
name|det
operator|.
name|getNumStates
argument_list|()
decl_stmt|;
if|if
condition|(
name|numStates
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  initial [shape=plaintext,label=\"0\"]\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"  initial -> 0\n"
argument_list|)
expr_stmt|;
block|}
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|state
init|=
literal|0
init|;
name|state
operator|<
name|numStates
condition|;
name|state
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|det
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" [shape=doublecircle,label=\""
operator|+
name|state
operator|+
literal|"\"]\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|" [shape=circle,label=\""
operator|+
name|state
operator|+
literal|"\"]\n"
argument_list|)
expr_stmt|;
block|}
name|int
name|numTransitions
init|=
name|det
operator|.
name|initTransition
argument_list|(
name|state
argument_list|,
name|t
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
name|numTransitions
condition|;
name|i
operator|++
control|)
block|{
name|det
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
assert|assert
name|t
operator|.
name|max
operator|>=
name|t
operator|.
name|min
assert|;
for|for
control|(
name|int
name|j
init|=
name|t
operator|.
name|min
init|;
name|j
operator|<=
name|t
operator|.
name|max
condition|;
name|j
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|t
operator|.
name|dest
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" [label=\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|==
name|anyTermID
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
name|idToTerm
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"\"]\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|b
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// TODO: should we impl rewrite to return BooleanQuery of PhraseQuery,
comment|// when 1) automaton is finite, 2) doesn't use ANY transition, 3) is
comment|// "small enough"?
DECL|class|EnumAndScorer
specifier|static
class|class
name|EnumAndScorer
block|{
DECL|field|termID
specifier|public
specifier|final
name|int
name|termID
decl_stmt|;
DECL|field|posEnum
specifier|public
specifier|final
name|PostingsEnum
name|posEnum
decl_stmt|;
comment|// How many positions left in the current document:
DECL|field|posLeft
specifier|public
name|int
name|posLeft
decl_stmt|;
comment|// Current position
DECL|field|pos
specifier|public
name|int
name|pos
decl_stmt|;
DECL|method|EnumAndScorer
specifier|public
name|EnumAndScorer
parameter_list|(
name|int
name|termID
parameter_list|,
name|PostingsEnum
name|posEnum
parameter_list|)
block|{
name|this
operator|.
name|termID
operator|=
name|termID
expr_stmt|;
name|this
operator|.
name|posEnum
operator|=
name|posEnum
expr_stmt|;
block|}
block|}
DECL|class|TermAutomatonWeight
specifier|final
class|class
name|TermAutomatonWeight
extends|extends
name|Weight
block|{
DECL|field|automaton
specifier|final
name|Automaton
name|automaton
decl_stmt|;
DECL|field|termStates
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|TermContext
argument_list|>
name|termStates
decl_stmt|;
DECL|field|stats
specifier|private
specifier|final
name|Similarity
operator|.
name|SimWeight
name|stats
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|method|TermAutomatonWeight
specifier|public
name|TermAutomatonWeight
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|TermContext
argument_list|>
name|termStates
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|TermAutomatonQuery
operator|.
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|automaton
operator|=
name|automaton
expr_stmt|;
name|this
operator|.
name|termStates
operator|=
name|termStates
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TermStatistics
argument_list|>
name|allTermStats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|BytesRef
argument_list|>
name|ent
range|:
name|idToTerm
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Integer
name|termID
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|allTermStats
operator|.
name|add
argument_list|(
name|searcher
operator|.
name|termStatistics
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|termStates
operator|.
name|get
argument_list|(
name|termID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|stats
operator|=
name|similarity
operator|.
name|computeWeight
argument_list|(
name|boost
argument_list|,
name|searcher
operator|.
name|collectionStatistics
argument_list|(
name|field
argument_list|)
argument_list|,
name|allTermStats
operator|.
name|toArray
argument_list|(
operator|new
name|TermStatistics
index|[
name|allTermStats
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
for|for
control|(
name|BytesRef
name|text
range|:
name|termToID
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"weight("
operator|+
name|TermAutomatonQuery
operator|.
name|this
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Initialize the enums; null for a given slot means that term didn't appear in this reader
name|EnumAndScorer
index|[]
name|enums
init|=
operator|new
name|EnumAndScorer
index|[
name|idToTerm
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|boolean
name|any
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|TermContext
argument_list|>
name|ent
range|:
name|termStates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|TermContext
name|termContext
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
assert|assert
name|termContext
operator|.
name|wasBuiltFor
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
argument_list|)
operator|:
literal|"The top-reader used to create Weight is not the same as the current reader's top-reader ("
operator|+
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
assert|;
name|BytesRef
name|term
init|=
name|idToTerm
operator|.
name|get
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|TermState
name|state
init|=
name|termContext
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|enums
index|[
name|ent
operator|.
name|getKey
argument_list|()
index|]
operator|=
operator|new
name|EnumAndScorer
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|POSITIONS
argument_list|)
argument_list|)
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|any
condition|)
block|{
return|return
operator|new
name|TermAutomatonScorer
argument_list|(
name|this
argument_list|,
name|enums
argument_list|,
name|anyTermID
argument_list|,
name|idToTerm
argument_list|,
name|similarity
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|Operations
operator|.
name|isEmpty
argument_list|(
name|det
argument_list|)
condition|)
block|{
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
name|IntsRef
name|single
init|=
name|Operations
operator|.
name|getSingleton
argument_list|(
name|det
argument_list|)
decl_stmt|;
if|if
condition|(
name|single
operator|!=
literal|null
operator|&&
name|single
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|idToTerm
operator|.
name|get
argument_list|(
name|single
operator|.
name|ints
index|[
name|single
operator|.
name|offset
index|]
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|// TODO: can PhraseQuery really handle multiple terms at the same position?  If so, why do we even have MultiPhraseQuery?
comment|// Try for either PhraseQuery or MultiPhraseQuery, which only works when the automaton is a sausage:
name|MultiPhraseQuery
operator|.
name|Builder
name|mpq
init|=
operator|new
name|MultiPhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|pq
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
name|int
name|state
init|=
literal|0
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|query
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|count
init|=
name|det
operator|.
name|initTransition
argument_list|(
name|state
argument_list|,
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|det
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
operator|==
literal|false
condition|)
block|{
name|mpq
operator|=
literal|null
expr_stmt|;
name|pq
operator|=
literal|null
expr_stmt|;
block|}
break|break;
block|}
elseif|else
if|if
condition|(
name|det
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|mpq
operator|=
literal|null
expr_stmt|;
name|pq
operator|=
literal|null
expr_stmt|;
break|break;
block|}
name|int
name|dest
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|matchesAny
init|=
literal|false
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|det
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|dest
operator|=
name|t
operator|.
name|dest
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dest
operator|!=
name|t
operator|.
name|dest
condition|)
block|{
name|mpq
operator|=
literal|null
expr_stmt|;
name|pq
operator|=
literal|null
expr_stmt|;
break|break
name|query
break|;
block|}
name|matchesAny
operator||=
name|anyTermID
operator|>=
name|t
operator|.
name|min
operator|&&
name|anyTermID
operator|<=
name|t
operator|.
name|max
expr_stmt|;
if|if
condition|(
name|matchesAny
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|int
name|termID
init|=
name|t
operator|.
name|min
init|;
name|termID
operator|<=
name|t
operator|.
name|max
condition|;
name|termID
operator|++
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|idToTerm
operator|.
name|get
argument_list|(
name|termID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|matchesAny
operator|==
literal|false
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|pq
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|pq
operator|.
name|add
argument_list|(
name|terms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pq
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
name|state
operator|=
name|dest
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|pq
operator|!=
literal|null
condition|)
block|{
return|return
name|pq
operator|.
name|build
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|mpq
operator|!=
literal|null
condition|)
block|{
return|return
name|mpq
operator|.
name|build
argument_list|()
return|;
block|}
comment|// TODO: we could maybe also rewrite to union of PhraseQuery (pull all finite strings) if it's "worth it"?
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

