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
name|Closeable
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
comment|/**  * Analyzes the text, producing a single {@link OffsetsEnum} wrapping the {@link TokenStream} filtered to terms  * in the query, including wildcards.  It can't handle position-sensitive queries (phrases). Passage accuracy suffers  * because the freq() is unknown -- it's always {@link Integer#MAX_VALUE} instead.  */
end_comment

begin_class
DECL|class|TokenStreamOffsetStrategy
specifier|public
class|class
name|TokenStreamOffsetStrategy
extends|extends
name|AnalysisOffsetStrategy
block|{
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
DECL|method|TokenStreamOffsetStrategy
specifier|public
name|TokenStreamOffsetStrategy
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
index|[]
name|terms
parameter_list|,
name|PhraseHelper
name|phraseHelper
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
parameter_list|,
name|Analyzer
name|indexAnalyzer
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|ZERO_LEN_BYTES_REF_ARRAY
argument_list|,
name|phraseHelper
argument_list|,
name|convertTermsToAutomata
argument_list|(
name|terms
argument_list|,
name|automata
argument_list|)
argument_list|,
name|indexAnalyzer
argument_list|)
expr_stmt|;
assert|assert
name|phraseHelper
operator|.
name|hasPositionSensitivity
argument_list|()
operator|==
literal|false
assert|;
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
name|String
name|termString
init|=
name|terms
index|[
name|i
index|]
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|newAutomata
index|[
name|i
index|]
operator|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|termString
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|termString
return|;
block|}
block|}
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
name|TokenStream
name|tokenStream
init|=
name|tokenStream
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|PostingsEnum
name|mtqPostingsEnum
init|=
operator|new
name|TokenStreamPostingsEnum
argument_list|(
name|tokenStream
argument_list|,
name|automata
argument_list|)
decl_stmt|;
name|mtqPostingsEnum
operator|.
name|advance
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|OffsetsEnum
argument_list|(
literal|null
argument_list|,
name|mtqPostingsEnum
argument_list|)
argument_list|)
return|;
block|}
comment|// See class javadocs.
comment|// TODO: DWS perhaps instead OffsetsEnum could become abstract and this would be an impl?  See TODOs in OffsetsEnum.
DECL|class|TokenStreamPostingsEnum
specifier|private
specifier|static
class|class
name|TokenStreamPostingsEnum
extends|extends
name|PostingsEnum
implements|implements
name|Closeable
block|{
DECL|field|stream
name|TokenStream
name|stream
decl_stmt|;
comment|// becomes null when closed
DECL|field|matchers
specifier|final
name|CharacterRunAutomaton
index|[]
name|matchers
decl_stmt|;
DECL|field|charTermAtt
specifier|final
name|CharTermAttribute
name|charTermAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|final
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|currentDoc
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentMatch
name|int
name|currentMatch
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentStartOffset
name|int
name|currentStartOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentEndOffset
name|int
name|currentEndOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|matchDescriptions
specifier|final
name|BytesRef
name|matchDescriptions
index|[]
decl_stmt|;
DECL|method|TokenStreamPostingsEnum
name|TokenStreamPostingsEnum
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|matchers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stream
operator|=
name|ts
expr_stmt|;
name|this
operator|.
name|matchers
operator|=
name|matchers
expr_stmt|;
name|matchDescriptions
operator|=
operator|new
name|BytesRef
index|[
name|matchers
operator|.
name|length
index|]
expr_stmt|;
name|charTermAtt
operator|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
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
name|matchers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|matchers
index|[
name|i
index|]
operator|.
name|run
argument_list|(
name|charTermAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|charTermAtt
operator|.
name|length
argument_list|()
argument_list|)
condition|)
block|{
name|currentStartOffset
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|currentEndOffset
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
name|currentMatch
operator|=
name|i
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
comment|// exhausted
name|currentStartOffset
operator|=
name|currentEndOffset
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
comment|// lie
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|currentStartOffset
operator|>=
literal|0
assert|;
return|return
name|currentStartOffset
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|currentEndOffset
operator|>=
literal|0
assert|;
return|return
name|currentEndOffset
return|;
block|}
comment|// TOTAL HACK; used in OffsetsEnum.getTerm()
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|matchDescriptions
index|[
name|currentMatch
index|]
operator|==
literal|null
condition|)
block|{
name|matchDescriptions
index|[
name|currentMatch
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|matchers
index|[
name|currentMatch
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|matchDescriptions
index|[
name|currentMatch
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|currentDoc
operator|=
name|target
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
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
name|stream
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

