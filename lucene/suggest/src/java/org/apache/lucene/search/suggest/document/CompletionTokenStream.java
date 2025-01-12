begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|TokenStreamToAutomaton
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|AttributeImpl
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
name|AttributeReflector
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
name|CharsRefBuilder
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
name|IOUtils
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
name|FiniteStringsIterator
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
name|LimitedFiniteStringsIterator
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionAnalyzer
operator|.
name|DEFAULT_MAX_GRAPH_EXPANSIONS
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
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionAnalyzer
operator|.
name|DEFAULT_PRESERVE_POSITION_INCREMENTS
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
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionAnalyzer
operator|.
name|DEFAULT_PRESERVE_SEP
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
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
import|;
end_import

begin_comment
comment|/**  * Token stream which converts a provided token stream to an automaton.  * The accepted strings enumeration from the automaton are available through the  * {@link org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute} attribute  * The token stream uses a {@link org.apache.lucene.analysis.tokenattributes.PayloadAttribute} to store  * a completion's payload (see {@link CompletionTokenStream#setPayload(org.apache.lucene.util.BytesRef)})  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompletionTokenStream
specifier|public
specifier|final
class|class
name|CompletionTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|payloadAttr
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAttr
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bytesAtt
specifier|private
specifier|final
name|BytesRefBuilderTermAttribute
name|bytesAtt
init|=
name|addAttribute
argument_list|(
name|BytesRefBuilderTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|inputTokenStream
specifier|final
name|TokenStream
name|inputTokenStream
decl_stmt|;
DECL|field|preserveSep
specifier|final
name|boolean
name|preserveSep
decl_stmt|;
DECL|field|preservePositionIncrements
specifier|final
name|boolean
name|preservePositionIncrements
decl_stmt|;
DECL|field|maxGraphExpansions
specifier|final
name|int
name|maxGraphExpansions
decl_stmt|;
DECL|field|finiteStrings
specifier|private
name|FiniteStringsIterator
name|finiteStrings
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
DECL|field|charTermAttribute
specifier|private
name|CharTermAttribute
name|charTermAttribute
decl_stmt|;
comment|/**    * Creates a token stream to convert<code>input</code> to a token stream    * of accepted strings by its automaton.    *<p>    * The token stream<code>input</code> is converted to an automaton    * with the default settings of {@link org.apache.lucene.search.suggest.document.CompletionAnalyzer}    */
DECL|method|CompletionTokenStream
name|CompletionTokenStream
parameter_list|(
name|TokenStream
name|inputTokenStream
parameter_list|)
block|{
name|this
argument_list|(
name|inputTokenStream
argument_list|,
name|DEFAULT_PRESERVE_SEP
argument_list|,
name|DEFAULT_PRESERVE_POSITION_INCREMENTS
argument_list|,
name|DEFAULT_MAX_GRAPH_EXPANSIONS
argument_list|)
expr_stmt|;
block|}
DECL|method|CompletionTokenStream
name|CompletionTokenStream
parameter_list|(
name|TokenStream
name|inputTokenStream
parameter_list|,
name|boolean
name|preserveSep
parameter_list|,
name|boolean
name|preservePositionIncrements
parameter_list|,
name|int
name|maxGraphExpansions
parameter_list|)
block|{
comment|// Don't call the super(input) ctor - this is a true delegate and has a new attribute source since we consume
comment|// the input stream entirely in the first call to incrementToken
name|this
operator|.
name|inputTokenStream
operator|=
name|inputTokenStream
expr_stmt|;
name|this
operator|.
name|preserveSep
operator|=
name|preserveSep
expr_stmt|;
name|this
operator|.
name|preservePositionIncrements
operator|=
name|preservePositionIncrements
expr_stmt|;
name|this
operator|.
name|maxGraphExpansions
operator|=
name|maxGraphExpansions
expr_stmt|;
block|}
comment|/**    * Sets a payload available throughout successive token stream enumeration    */
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|finiteStrings
operator|==
literal|null
condition|)
block|{
name|Automaton
name|automaton
init|=
name|toAutomaton
argument_list|()
decl_stmt|;
name|finiteStrings
operator|=
operator|new
name|LimitedFiniteStringsIterator
argument_list|(
name|automaton
argument_list|,
name|maxGraphExpansions
argument_list|)
expr_stmt|;
block|}
name|IntsRef
name|string
init|=
name|finiteStrings
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|string
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Util
operator|.
name|toBytesRef
argument_list|(
name|string
argument_list|,
name|bytesAtt
operator|.
name|builder
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we have UTF-8
if|if
condition|(
name|charTermAttribute
operator|!=
literal|null
condition|)
block|{
name|charTermAttribute
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|charTermAttribute
operator|.
name|append
argument_list|(
name|bytesAtt
operator|.
name|toUTF16
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|payloadAttr
operator|.
name|setPayload
argument_list|(
name|this
operator|.
name|payload
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
name|finiteStrings
operator|==
literal|null
condition|)
block|{
name|inputTokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|finiteStrings
operator|==
literal|null
condition|)
block|{
name|inputTokenStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// we only create this if we really need it to safe the UTF-8 to UTF-16 conversion
name|charTermAttribute
operator|=
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|finiteStrings
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Converts the token stream to an automaton,    * treating the transition labels as utf-8    */
DECL|method|toAutomaton
specifier|public
name|Automaton
name|toAutomaton
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|toAutomaton
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Converts the tokenStream to an automaton    */
DECL|method|toAutomaton
specifier|public
name|Automaton
name|toAutomaton
parameter_list|(
name|boolean
name|unicodeAware
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO refactor this
comment|// maybe we could hook up a modified automaton from TermAutomatonQuery here?
name|Automaton
name|automaton
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Create corresponding automaton: labels are bytes
comment|// from each analyzed token, with byte 0 used as
comment|// separator between tokens:
specifier|final
name|TokenStreamToAutomaton
name|tsta
decl_stmt|;
if|if
condition|(
name|preserveSep
condition|)
block|{
name|tsta
operator|=
operator|new
name|EscapingTokenStreamToAutomaton
argument_list|(
operator|(
name|char
operator|)
name|SEP_LABEL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// When we're not preserving sep, we don't steal 0xff
comment|// byte, so we don't need to do any escaping:
name|tsta
operator|=
operator|new
name|TokenStreamToAutomaton
argument_list|()
expr_stmt|;
block|}
name|tsta
operator|.
name|setPreservePositionIncrements
argument_list|(
name|preservePositionIncrements
argument_list|)
expr_stmt|;
name|tsta
operator|.
name|setUnicodeArcs
argument_list|(
name|unicodeAware
argument_list|)
expr_stmt|;
name|automaton
operator|=
name|tsta
operator|.
name|toAutomaton
argument_list|(
name|inputTokenStream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|inputTokenStream
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we can optimize this somewhat by determinizing
comment|// while we convert
name|automaton
operator|=
name|replaceSep
argument_list|(
name|automaton
argument_list|,
name|preserveSep
argument_list|,
name|SEP_LABEL
argument_list|)
expr_stmt|;
comment|// This automaton should not blow up during determinize:
return|return
name|Operations
operator|.
name|determinize
argument_list|(
name|automaton
argument_list|,
name|maxGraphExpansions
argument_list|)
return|;
block|}
comment|/**    * Just escapes the 0xff byte (which we still for SEP).    */
DECL|class|EscapingTokenStreamToAutomaton
specifier|private
specifier|static
specifier|final
class|class
name|EscapingTokenStreamToAutomaton
extends|extends
name|TokenStreamToAutomaton
block|{
DECL|field|spare
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|sepLabel
specifier|private
name|char
name|sepLabel
decl_stmt|;
DECL|method|EscapingTokenStreamToAutomaton
specifier|public
name|EscapingTokenStreamToAutomaton
parameter_list|(
name|char
name|sepLabel
parameter_list|)
block|{
name|this
operator|.
name|sepLabel
operator|=
name|sepLabel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|changeToken
specifier|protected
name|BytesRef
name|changeToken
parameter_list|(
name|BytesRef
name|in
parameter_list|)
block|{
name|int
name|upto
init|=
literal|0
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
name|in
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|in
operator|.
name|bytes
index|[
name|in
operator|.
name|offset
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|==
operator|(
name|byte
operator|)
name|sepLabel
condition|)
block|{
name|spare
operator|.
name|grow
argument_list|(
name|upto
operator|+
literal|2
argument_list|)
expr_stmt|;
name|spare
operator|.
name|setByteAt
argument_list|(
name|upto
operator|++
argument_list|,
operator|(
name|byte
operator|)
name|sepLabel
argument_list|)
expr_stmt|;
name|spare
operator|.
name|setByteAt
argument_list|(
name|upto
operator|++
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|spare
operator|.
name|grow
argument_list|(
name|upto
operator|+
literal|1
argument_list|)
expr_stmt|;
name|spare
operator|.
name|setByteAt
argument_list|(
name|upto
operator|++
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
name|spare
operator|.
name|setLength
argument_list|(
name|upto
argument_list|)
expr_stmt|;
return|return
name|spare
operator|.
name|get
argument_list|()
return|;
block|}
block|}
comment|// Replaces SEP with epsilon or remaps them if
comment|// we were asked to preserve them:
DECL|method|replaceSep
specifier|private
specifier|static
name|Automaton
name|replaceSep
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|boolean
name|preserveSep
parameter_list|,
name|int
name|sepLabel
parameter_list|)
block|{
name|Automaton
name|result
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
comment|// Copy all states over
name|int
name|numStates
init|=
name|a
operator|.
name|getNumStates
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|0
init|;
name|s
operator|<
name|numStates
condition|;
name|s
operator|++
control|)
block|{
name|result
operator|.
name|createState
argument_list|()
expr_stmt|;
name|result
operator|.
name|setAccept
argument_list|(
name|s
argument_list|,
name|a
operator|.
name|isAccept
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Go in reverse topo sort so we know we only have to
comment|// make one pass:
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
name|int
index|[]
name|topoSortStates
init|=
name|Operations
operator|.
name|topoSortStates
argument_list|(
name|a
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
name|topoSortStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|state
init|=
name|topoSortStates
index|[
name|topoSortStates
operator|.
name|length
operator|-
literal|1
operator|-
name|i
index|]
decl_stmt|;
name|int
name|count
init|=
name|a
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
name|a
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|min
operator|==
name|TokenStreamToAutomaton
operator|.
name|POS_SEP
condition|)
block|{
assert|assert
name|t
operator|.
name|max
operator|==
name|TokenStreamToAutomaton
operator|.
name|POS_SEP
assert|;
if|if
condition|(
name|preserveSep
condition|)
block|{
comment|// Remap to SEP_LABEL:
name|result
operator|.
name|addTransition
argument_list|(
name|state
argument_list|,
name|t
operator|.
name|dest
argument_list|,
name|sepLabel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|addEpsilon
argument_list|(
name|state
argument_list|,
name|t
operator|.
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|min
operator|==
name|TokenStreamToAutomaton
operator|.
name|HOLE
condition|)
block|{
assert|assert
name|t
operator|.
name|max
operator|==
name|TokenStreamToAutomaton
operator|.
name|HOLE
assert|;
comment|// Just remove the hole: there will then be two
comment|// SEP tokens next to each other, which will only
comment|// match another hole at search time.  Note that
comment|// it will also match an empty-string token ... if
comment|// that's somehow a problem we can always map HOLE
comment|// to a dedicated byte (and escape it in the
comment|// input).
name|result
operator|.
name|addEpsilon
argument_list|(
name|state
argument_list|,
name|t
operator|.
name|dest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|addTransition
argument_list|(
name|state
argument_list|,
name|t
operator|.
name|dest
argument_list|,
name|t
operator|.
name|min
argument_list|,
name|t
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|finishState
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Attribute providing access to the term builder and UTF-16 conversion    */
DECL|interface|BytesRefBuilderTermAttribute
specifier|public
interface|interface
name|BytesRefBuilderTermAttribute
extends|extends
name|TermToBytesRefAttribute
block|{
comment|/**      * Returns the builder from which the term is derived.      */
DECL|method|builder
name|BytesRefBuilder
name|builder
parameter_list|()
function_decl|;
comment|/**      * Returns the term represented as UTF-16      */
DECL|method|toUTF16
name|CharSequence
name|toUTF16
parameter_list|()
function_decl|;
block|}
comment|/**    * Custom attribute implementation for completion token stream    */
DECL|class|BytesRefBuilderTermAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|BytesRefBuilderTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|BytesRefBuilderTermAttribute
implements|,
name|TermToBytesRefAttribute
block|{
DECL|field|bytes
specifier|private
specifier|final
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|charsRef
specifier|private
specifier|transient
name|CharsRefBuilder
name|charsRef
decl_stmt|;
comment|/**      * Sole constructor      * no-op      */
DECL|method|BytesRefBuilderTermAttributeImpl
specifier|public
name|BytesRefBuilderTermAttributeImpl
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|builder
specifier|public
name|BytesRefBuilder
name|builder
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|bytes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|BytesRefBuilderTermAttributeImpl
name|other
init|=
operator|(
name|BytesRefBuilderTermAttributeImpl
operator|)
name|target
decl_stmt|;
name|other
operator|.
name|bytes
operator|.
name|copyBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|AttributeImpl
name|clone
parameter_list|()
block|{
name|BytesRefBuilderTermAttributeImpl
name|other
init|=
operator|new
name|BytesRefBuilderTermAttributeImpl
argument_list|()
decl_stmt|;
name|copyTo
argument_list|(
name|other
argument_list|)
expr_stmt|;
return|return
name|other
return|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|reflector
operator|.
name|reflect
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|,
literal|"bytes"
argument_list|,
name|getBytesRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toUTF16
specifier|public
name|CharSequence
name|toUTF16
parameter_list|()
block|{
if|if
condition|(
name|charsRef
operator|==
literal|null
condition|)
block|{
name|charsRef
operator|=
operator|new
name|CharsRefBuilder
argument_list|()
expr_stmt|;
block|}
name|charsRef
operator|.
name|copyUTF8Bytes
argument_list|(
name|getBytesRef
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|charsRef
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

