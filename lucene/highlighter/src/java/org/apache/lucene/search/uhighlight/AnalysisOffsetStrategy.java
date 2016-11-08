begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|Analyzer
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
name|FilteringTokenFilter
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
name|LeafReader
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
name|Terms
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
name|memory
operator|.
name|MemoryIndex
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
name|Query
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
name|SpanQuery
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
name|automaton
operator|.
name|Automata
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
name|CharacterRunAutomaton
import|;
end_import

begin_comment
comment|/**  * Uses an {@link Analyzer} on content to get offsets. It may use a {@link MemoryIndex} too.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|AnalysisOffsetStrategy
specifier|public
class|class
name|AnalysisOffsetStrategy
extends|extends
name|FieldOffsetStrategy
block|{
comment|//TODO: Consider splitting this highlighter into a MemoryIndexFieldHighlighter and a TokenStreamFieldHighlighter
DECL|field|ZERO_LEN_BYTES_REF_ARRAY
specifier|private
specifier|static
specifier|final
name|BytesRef
index|[]
name|ZERO_LEN_BYTES_REF_ARRAY
init|=
operator|new
name|BytesRef
index|[
literal|0
index|]
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|memoryIndex
specifier|private
specifier|final
name|MemoryIndex
name|memoryIndex
decl_stmt|;
DECL|field|leafReader
specifier|private
specifier|final
name|LeafReader
name|leafReader
decl_stmt|;
DECL|field|preMemIndexFilterAutomaton
specifier|private
specifier|final
name|CharacterRunAutomaton
name|preMemIndexFilterAutomaton
decl_stmt|;
DECL|method|AnalysisOffsetStrategy
specifier|public
name|AnalysisOffsetStrategy
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
index|[]
name|extractedTerms
parameter_list|,
name|PhraseHelper
name|phraseHelper
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Function
argument_list|<
name|Query
argument_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
argument_list|>
name|multiTermQueryRewrite
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|extractedTerms
argument_list|,
name|phraseHelper
argument_list|,
name|automata
argument_list|)
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
comment|// Automata (Wildcards / MultiTermQuery):
name|this
operator|.
name|automata
operator|=
name|automata
expr_stmt|;
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|0
operator|&&
operator|!
name|strictPhrases
operator|.
name|hasPositionSensitivity
argument_list|()
condition|)
block|{
name|this
operator|.
name|automata
operator|=
name|convertTermsToAutomata
argument_list|(
name|terms
argument_list|,
name|automata
argument_list|)
expr_stmt|;
comment|// clear the terms array now that we've moved them to be expressed as automata
name|terms
operator|=
name|ZERO_LEN_BYTES_REF_ARRAY
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|0
operator|||
name|strictPhrases
operator|.
name|willRewrite
argument_list|()
condition|)
block|{
comment|//needs MemoryIndex
comment|// init MemoryIndex
name|boolean
name|storePayloads
init|=
name|strictPhrases
operator|.
name|hasPositionSensitivity
argument_list|()
decl_stmt|;
comment|// might be needed
name|memoryIndex
operator|=
operator|new
name|MemoryIndex
argument_list|(
literal|true
argument_list|,
name|storePayloads
argument_list|)
expr_stmt|;
comment|//true==store offsets
name|leafReader
operator|=
operator|(
name|LeafReader
operator|)
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
comment|// preFilter for MemoryIndex
name|preMemIndexFilterAutomaton
operator|=
name|buildCombinedAutomaton
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|this
operator|.
name|automata
argument_list|,
name|strictPhrases
argument_list|,
name|multiTermQueryRewrite
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|memoryIndex
operator|=
literal|null
expr_stmt|;
name|leafReader
operator|=
literal|null
expr_stmt|;
name|preMemIndexFilterAutomaton
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getOffsetSource
specifier|public
name|UnifiedHighlighter
operator|.
name|OffsetSource
name|getOffsetSource
parameter_list|()
block|{
return|return
name|UnifiedHighlighter
operator|.
name|OffsetSource
operator|.
name|ANALYSIS
return|;
block|}
annotation|@
name|Override
DECL|method|getOffsetsEnums
specifier|public
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|getOffsetsEnums
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
comment|// note: don't need LimitTokenOffsetFilter since content is already truncated to maxLength
name|TokenStream
name|tokenStream
init|=
name|tokenStream
argument_list|(
name|content
argument_list|)
decl_stmt|;
if|if
condition|(
name|memoryIndex
operator|!=
literal|null
condition|)
block|{
comment|// also handles automata.length> 0
comment|// We use a MemoryIndex and index the tokenStream so that later we have the PostingsEnum with offsets.
comment|// note: An *alternative* strategy is to get PostingsEnums without offsets from the main index
comment|//  and then marry this up with a fake PostingsEnum backed by a TokenStream (which has the offsets) and
comment|//  can use that to filter applicable tokens?  It would have the advantage of being able to exit
comment|//  early and save some re-analysis.  This would be an additional method/offset-source approach
comment|//  since it's still useful to highlight without any index (so we build MemoryIndex).
comment|// note: probably unwise to re-use TermsEnum on reset mem index so we don't. But we do re-use the
comment|//   leaf reader, which is a bit more top level than in the guts.
name|memoryIndex
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Filter the tokenStream to applicable terms
if|if
condition|(
name|preMemIndexFilterAutomaton
operator|!=
literal|null
condition|)
block|{
name|tokenStream
operator|=
name|newKeepWordFilter
argument_list|(
name|tokenStream
argument_list|,
name|preMemIndexFilterAutomaton
argument_list|)
expr_stmt|;
block|}
name|memoryIndex
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|tokenStream
argument_list|)
expr_stmt|;
comment|//note: calls tokenStream.reset()& close()
name|tokenStream
operator|=
literal|null
expr_stmt|;
comment|// it's consumed; done.
name|docId
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|automata
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|Terms
name|foundTerms
init|=
name|leafReader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|foundTerms
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
comment|//No offsets for this field.
block|}
comment|// Un-invert for the automata. Much more compact than a CachingTokenStream
name|tokenStream
operator|=
name|MultiTermHighlighting
operator|.
name|uninvertAndFilterTerms
argument_list|(
name|foundTerms
argument_list|,
literal|0
argument_list|,
name|automata
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|createOffsetsEnums
argument_list|(
name|leafReader
argument_list|,
name|docId
argument_list|,
name|tokenStream
argument_list|)
return|;
block|}
DECL|method|tokenStream
specifier|protected
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MultiValueTokenStream
operator|.
name|wrap
argument_list|(
name|field
argument_list|,
name|analyzer
argument_list|,
name|content
argument_list|,
name|UnifiedHighlighter
operator|.
name|MULTIVAL_SEP_CHAR
argument_list|)
return|;
block|}
DECL|method|convertTermsToAutomata
specifier|private
specifier|static
name|CharacterRunAutomaton
index|[]
name|convertTermsToAutomata
parameter_list|(
name|BytesRef
index|[]
name|terms
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
parameter_list|)
block|{
name|CharacterRunAutomaton
index|[]
name|newAutomata
init|=
operator|new
name|CharacterRunAutomaton
index|[
name|terms
operator|.
name|length
operator|+
name|automata
operator|.
name|length
index|]
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|newAutomata
index|[
name|i
index|]
operator|=
name|MultiTermHighlighting
operator|.
name|makeStringMatchAutomata
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Append existing automata (that which is used for MTQs)
name|System
operator|.
name|arraycopy
argument_list|(
name|automata
argument_list|,
literal|0
argument_list|,
name|newAutomata
argument_list|,
name|terms
operator|.
name|length
argument_list|,
name|automata
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newAutomata
return|;
block|}
DECL|method|newKeepWordFilter
specifier|private
specifier|static
name|FilteringTokenFilter
name|newKeepWordFilter
parameter_list|(
specifier|final
name|TokenStream
name|tokenStream
parameter_list|,
specifier|final
name|CharacterRunAutomaton
name|charRunAutomaton
parameter_list|)
block|{
comment|// it'd be nice to use KeepWordFilter but it demands a CharArraySet. TODO File JIRA? Need a new interface?
return|return
operator|new
name|FilteringTokenFilter
argument_list|(
name|tokenStream
argument_list|)
block|{
specifier|final
name|CharTermAttribute
name|charAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|accept
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|charRunAutomaton
operator|.
name|run
argument_list|(
name|charAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|charAtt
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Build one {@link CharacterRunAutomaton} matching any term the query might match.    */
DECL|method|buildCombinedAutomaton
specifier|private
specifier|static
name|CharacterRunAutomaton
name|buildCombinedAutomaton
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
index|[]
name|terms
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
parameter_list|,
name|PhraseHelper
name|strictPhrases
parameter_list|,
name|Function
argument_list|<
name|Query
argument_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
argument_list|>
name|multiTermQueryRewrite
parameter_list|)
block|{
name|List
argument_list|<
name|CharacterRunAutomaton
argument_list|>
name|allAutomata
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|allAutomata
operator|.
name|add
argument_list|(
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|addAll
argument_list|(
name|allAutomata
argument_list|,
name|automata
argument_list|)
expr_stmt|;
for|for
control|(
name|SpanQuery
name|spanQuery
range|:
name|strictPhrases
operator|.
name|getSpanQueries
argument_list|()
control|)
block|{
name|Collections
operator|.
name|addAll
argument_list|(
name|allAutomata
argument_list|,
name|MultiTermHighlighting
operator|.
name|extractAutomata
argument_list|(
name|spanQuery
argument_list|,
name|field
argument_list|,
literal|true
argument_list|,
name|multiTermQueryRewrite
argument_list|)
argument_list|)
expr_stmt|;
comment|//true==lookInSpan
block|}
if|if
condition|(
name|allAutomata
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|allAutomata
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|//TODO it'd be nice if we could get at the underlying Automaton in CharacterRunAutomaton so that we
comment|//  could union them all. But it's not exposed, and note TermRangeQuery isn't modelled as an Automaton
comment|//  by MultiTermHighlighting.
comment|// Return an aggregate CharacterRunAutomaton of others
return|return
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Automata
operator|.
name|makeEmpty
argument_list|()
argument_list|)
block|{
comment|// the makeEmpty() is bogus; won't be used
annotation|@
name|Override
specifier|public
name|boolean
name|run
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allAutomata
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// don't use foreach to avoid Iterator allocation
if|if
condition|(
name|allAutomata
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|run
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

