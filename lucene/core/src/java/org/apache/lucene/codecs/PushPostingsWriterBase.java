begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|FieldInfo
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
name|IndexOptions
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
name|FixedBitSet
import|;
end_import

begin_comment
comment|/**  * Extension of {@link PostingsWriterBase}, adding a push  * API for writing each element of the postings.  This API  * is somewhat analagous to an XML SAX API, while {@link  * PostingsWriterBase} is more like an XML DOM API.  *   * @see PostingsReaderBase  * @lucene.experimental  */
end_comment

begin_comment
comment|// TODO: find a better name; this defines the API that the
end_comment

begin_comment
comment|// terms dict impls use to talk to a postings impl.
end_comment

begin_comment
comment|// TermsDict + PostingsReader/WriterBase == PostingsConsumer/Producer
end_comment

begin_class
DECL|class|PushPostingsWriterBase
specifier|public
specifier|abstract
class|class
name|PushPostingsWriterBase
extends|extends
name|PostingsWriterBase
block|{
comment|// Reused in writeTerm
DECL|field|postingsEnum
specifier|private
name|PostingsEnum
name|postingsEnum
decl_stmt|;
DECL|field|enumFlags
specifier|private
name|int
name|enumFlags
decl_stmt|;
comment|/** {@link FieldInfo} of current field being written. */
DECL|field|fieldInfo
specifier|protected
name|FieldInfo
name|fieldInfo
decl_stmt|;
comment|/** {@link IndexOptions} of current field being       written */
DECL|field|indexOptions
specifier|protected
name|IndexOptions
name|indexOptions
decl_stmt|;
comment|/** True if the current field writes freqs. */
DECL|field|writeFreqs
specifier|protected
name|boolean
name|writeFreqs
decl_stmt|;
comment|/** True if the current field writes positions. */
DECL|field|writePositions
specifier|protected
name|boolean
name|writePositions
decl_stmt|;
comment|/** True if the current field writes payloads. */
DECL|field|writePayloads
specifier|protected
name|boolean
name|writePayloads
decl_stmt|;
comment|/** True if the current field writes offsets. */
DECL|field|writeOffsets
specifier|protected
name|boolean
name|writeOffsets
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|PushPostingsWriterBase
specifier|protected
name|PushPostingsWriterBase
parameter_list|()
block|{   }
comment|/** Return a newly created empty TermState */
DECL|method|newTermState
specifier|public
specifier|abstract
name|BlockTermState
name|newTermState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Start a new term.  Note that a matching call to {@link    *  #finishTerm(BlockTermState)} is done, only if the term has at least one    *  document. */
DECL|method|startTerm
specifier|public
specifier|abstract
name|void
name|startTerm
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Finishes the current term.  The provided {@link    *  BlockTermState} contains the term's summary statistics,     *  and will holds metadata from PBF when returned */
DECL|method|finishTerm
specifier|public
specifier|abstract
name|void
name|finishTerm
parameter_list|(
name|BlockTermState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Sets the current field for writing, and returns the    * fixed length of long[] metadata (which is fixed per    * field), called when the writing switches to another field. */
annotation|@
name|Override
DECL|method|setField
specifier|public
name|int
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|indexOptions
operator|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
expr_stmt|;
name|writeFreqs
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|writePositions
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|writeOffsets
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|writePayloads
operator|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
if|if
condition|(
name|writeFreqs
operator|==
literal|false
condition|)
block|{
name|enumFlags
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|writePositions
operator|==
literal|false
condition|)
block|{
name|enumFlags
operator|=
name|PostingsEnum
operator|.
name|FREQS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|writeOffsets
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|writePayloads
condition|)
block|{
name|enumFlags
operator|=
name|PostingsEnum
operator|.
name|PAYLOADS
expr_stmt|;
block|}
else|else
block|{
name|enumFlags
operator|=
name|PostingsEnum
operator|.
name|POSITIONS
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|writePayloads
condition|)
block|{
name|enumFlags
operator|=
name|PostingsEnum
operator|.
name|PAYLOADS
operator||
name|PostingsEnum
operator|.
name|OFFSETS
expr_stmt|;
block|}
else|else
block|{
name|enumFlags
operator|=
name|PostingsEnum
operator|.
name|OFFSETS
expr_stmt|;
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|writeTerm
specifier|public
specifier|final
name|BlockTermState
name|writeTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|FixedBitSet
name|docsSeen
parameter_list|)
throws|throws
name|IOException
block|{
name|startTerm
argument_list|()
expr_stmt|;
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|enumFlags
argument_list|)
expr_stmt|;
assert|assert
name|postingsEnum
operator|!=
literal|null
assert|;
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
name|long
name|totalTermFreq
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|docID
init|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|PostingsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|docFreq
operator|++
expr_stmt|;
name|docsSeen
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|int
name|freq
decl_stmt|;
if|if
condition|(
name|writeFreqs
condition|)
block|{
name|freq
operator|=
name|postingsEnum
operator|.
name|freq
argument_list|()
expr_stmt|;
name|totalTermFreq
operator|+=
name|freq
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|startDoc
argument_list|(
name|docID
argument_list|,
name|freq
argument_list|)
expr_stmt|;
if|if
condition|(
name|writePositions
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|int
name|pos
init|=
name|postingsEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|BytesRef
name|payload
init|=
name|writePayloads
condition|?
name|postingsEnum
operator|.
name|getPayload
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|startOffset
decl_stmt|;
name|int
name|endOffset
decl_stmt|;
if|if
condition|(
name|writeOffsets
condition|)
block|{
name|startOffset
operator|=
name|postingsEnum
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|postingsEnum
operator|.
name|endOffset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|endOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|addPosition
argument_list|(
name|pos
argument_list|,
name|payload
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
name|finishDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docFreq
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
name|BlockTermState
name|state
init|=
name|newTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|state
operator|.
name|totalTermFreq
operator|=
name|writeFreqs
condition|?
name|totalTermFreq
else|:
operator|-
literal|1
expr_stmt|;
name|finishTerm
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
comment|/** Adds a new doc in this term.     *<code>freq</code> will be -1 when term frequencies are omitted    * for the field. */
DECL|method|startDoc
specifier|public
specifier|abstract
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Add a new position and payload, and start/end offset.  A    *  null payload means no payload; a non-null payload with    *  zero length also means no payload.  Caller may reuse    *  the {@link BytesRef} for the payload between calls    *  (method must fully consume the payload).<code>startOffset</code>    *  and<code>endOffset</code> will be -1 when offsets are not indexed. */
DECL|method|addPosition
specifier|public
specifier|abstract
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when we are done adding positions and payloads    *  for each doc. */
DECL|method|finishDoc
specifier|public
specifier|abstract
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

