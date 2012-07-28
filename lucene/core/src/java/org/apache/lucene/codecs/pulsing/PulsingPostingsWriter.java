begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.pulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
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
name|ArrayList
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
name|codecs
operator|.
name|CodecUtil
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
name|codecs
operator|.
name|PostingsWriterBase
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
name|codecs
operator|.
name|TermStats
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
name|FieldInfo
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
name|store
operator|.
name|IndexOutput
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
name|store
operator|.
name|RAMOutputStream
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

begin_comment
comment|// TODO: we now inline based on total TF of the term,
end_comment

begin_comment
comment|// but it might be better to inline by "net bytes used"
end_comment

begin_comment
comment|// so that a term that has only 1 posting but a huge
end_comment

begin_comment
comment|// payload would not be inlined.  Though this is
end_comment

begin_comment
comment|// presumably rare in practice...
end_comment

begin_comment
comment|/**   * Writer for the pulsing format.   *<p>  * Wraps another postings implementation and decides   * (based on total number of occurrences), whether a terms   * postings should be inlined into the term dictionary,  * or passed through to the wrapped writer.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PulsingPostingsWriter
specifier|public
specifier|final
class|class
name|PulsingPostingsWriter
extends|extends
name|PostingsWriterBase
block|{
DECL|field|CODEC
specifier|final
specifier|static
name|String
name|CODEC
init|=
literal|"PulsedPostingsWriter"
decl_stmt|;
comment|// To add a new version, increment from the last one, and
comment|// change VERSION_CURRENT to point to your new version:
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|termsOut
specifier|private
name|IndexOutput
name|termsOut
decl_stmt|;
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
DECL|class|PendingTerm
specifier|private
specifier|static
class|class
name|PendingTerm
block|{
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|method|PendingTerm
specifier|public
name|PendingTerm
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
block|}
DECL|field|pendingTerms
specifier|private
specifier|final
name|List
argument_list|<
name|PendingTerm
argument_list|>
name|pendingTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|PendingTerm
argument_list|>
argument_list|()
decl_stmt|;
comment|// one entry per position
DECL|field|pending
specifier|private
specifier|final
name|Position
index|[]
name|pending
decl_stmt|;
DECL|field|pendingCount
specifier|private
name|int
name|pendingCount
init|=
literal|0
decl_stmt|;
comment|// -1 once we've hit too many positions
DECL|field|currentDoc
specifier|private
name|Position
name|currentDoc
decl_stmt|;
comment|// first Position entry of current doc
DECL|class|Position
specifier|private
specifier|static
specifier|final
class|class
name|Position
block|{
DECL|field|payload
name|BytesRef
name|payload
decl_stmt|;
DECL|field|termFreq
name|int
name|termFreq
decl_stmt|;
comment|// only incremented on first position for a given doc
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
block|}
comment|// TODO: -- lazy init this?  ie, if every single term
comment|// was inlined (eg for a "primary key" field) then we
comment|// never need to use this fallback?  Fallback writer for
comment|// non-inlined terms:
DECL|field|wrappedPostingsWriter
specifier|final
name|PostingsWriterBase
name|wrappedPostingsWriter
decl_stmt|;
comment|/** If the total number of positions (summed across all docs    *  for this term) is<= maxPositions, then the postings are    *  inlined into terms dict */
DECL|method|PulsingPostingsWriter
specifier|public
name|PulsingPostingsWriter
parameter_list|(
name|int
name|maxPositions
parameter_list|,
name|PostingsWriterBase
name|wrappedPostingsWriter
parameter_list|)
block|{
name|pending
operator|=
operator|new
name|Position
index|[
name|maxPositions
index|]
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
name|maxPositions
condition|;
name|i
operator|++
control|)
block|{
name|pending
index|[
name|i
index|]
operator|=
operator|new
name|Position
argument_list|()
expr_stmt|;
block|}
comment|// We simply wrap another postings writer, but only call
comment|// on it when tot positions is>= the cutoff:
name|this
operator|.
name|wrappedPostingsWriter
operator|=
name|wrappedPostingsWriter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsOut
operator|=
name|termsOut
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|termsOut
argument_list|,
name|CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeVInt
argument_list|(
name|pending
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// encode maxPositions in header
name|wrappedPostingsWriter
operator|.
name|start
argument_list|(
name|termsOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
comment|//if (DEBUG) System.out.println("PW   startTerm");
assert|assert
name|pendingCount
operator|==
literal|0
assert|;
block|}
comment|// TODO: -- should we NOT reuse across fields?  would
comment|// be cleaner
comment|// Currently, this instance is re-used across fields, so
comment|// our parent calls setField whenever the field changes
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("PW field=" + fieldInfo.name + " indexOptions=" + indexOptions);
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|wrappedPostingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|//DEBUG = BlockTreeTermsWriter.DEBUG;
block|}
DECL|field|DEBUG
specifier|private
name|boolean
name|DEBUG
decl_stmt|;
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|termDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|:
literal|"got docID="
operator|+
name|docID
assert|;
comment|/*     if (termID != -1) {       if (docID == 0) {         baseDocID = termID;       } else if (baseDocID + docID != termID) {         throw new RuntimeException("WRITE: baseDocID=" + baseDocID + " docID=" + docID + " termID=" + termID);       }     }     */
comment|//if (DEBUG) System.out.println("PW     doc=" + docID);
if|if
condition|(
name|pendingCount
operator|==
name|pending
operator|.
name|length
condition|)
block|{
name|push
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("PW: wrapped.finishDoc");
name|wrappedPostingsWriter
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pendingCount
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|pendingCount
operator|<
name|pending
operator|.
name|length
assert|;
name|currentDoc
operator|=
name|pending
index|[
name|pendingCount
index|]
expr_stmt|;
name|currentDoc
operator|.
name|docID
operator|=
name|docID
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|pendingCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
condition|)
block|{
name|pendingCount
operator|++
expr_stmt|;
name|currentDoc
operator|.
name|termFreq
operator|=
name|termDocFreq
expr_stmt|;
block|}
else|else
block|{
name|currentDoc
operator|.
name|termFreq
operator|=
name|termDocFreq
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// We've already seen too many docs for this term --
comment|// just forward to our fallback writer
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|docID
argument_list|,
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
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
block|{
comment|//if (DEBUG) System.out.println("PW       pos=" + position + " payload=" + (payload == null ? "null" : payload.length + " bytes"));
if|if
condition|(
name|pendingCount
operator|==
name|pending
operator|.
name|length
condition|)
block|{
name|push
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pendingCount
operator|==
operator|-
literal|1
condition|)
block|{
comment|// We've already seen too many docs for this term --
comment|// just forward to our fallback writer
name|wrappedPostingsWriter
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|payload
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// buffer up
specifier|final
name|Position
name|pos
init|=
name|pending
index|[
name|pendingCount
operator|++
index|]
decl_stmt|;
name|pos
operator|.
name|pos
operator|=
name|position
expr_stmt|;
name|pos
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|pos
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|pos
operator|.
name|docID
operator|=
name|currentDoc
operator|.
name|docID
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|pos
operator|.
name|payload
operator|==
literal|null
condition|)
block|{
name|pos
operator|.
name|payload
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pos
operator|.
name|payload
operator|.
name|copyBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pos
operator|.
name|payload
operator|!=
literal|null
condition|)
block|{
name|pos
operator|.
name|payload
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("PW     finishDoc");
if|if
condition|(
name|pendingCount
operator|==
operator|-
literal|1
condition|)
block|{
name|wrappedPostingsWriter
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|buffer
specifier|private
specifier|final
name|RAMOutputStream
name|buffer
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
comment|// private int baseDocID;
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("PW   finishTerm docCount=" + stats.docFreq + " pendingCount=" + pendingCount + " pendingTerms.size()=" + pendingTerms.size());
assert|assert
name|pendingCount
operator|>
literal|0
operator|||
name|pendingCount
operator|==
operator|-
literal|1
assert|;
if|if
condition|(
name|pendingCount
operator|==
operator|-
literal|1
condition|)
block|{
name|wrappedPostingsWriter
operator|.
name|finishTerm
argument_list|(
name|stats
argument_list|)
expr_stmt|;
comment|// Must add null entry to record terms that our
comment|// wrapped postings impl added
name|pendingTerms
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// There were few enough total occurrences for this
comment|// term, so we fully inline our postings data into
comment|// terms dict, now:
comment|// TODO: it'd be better to share this encoding logic
comment|// in some inner codec that knows how to write a
comment|// single doc / single position, etc.  This way if a
comment|// given codec wants to store other interesting
comment|// stuff, it could use this pulsing codec to do so
if|if
condition|(
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
condition|)
block|{
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
name|int
name|pendingIDX
init|=
literal|0
decl_stmt|;
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastOffsetLength
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|pendingIDX
operator|<
name|pendingCount
condition|)
block|{
specifier|final
name|Position
name|doc
init|=
name|pending
index|[
name|pendingIDX
index|]
decl_stmt|;
specifier|final
name|int
name|delta
init|=
name|doc
operator|.
name|docID
operator|-
name|lastDocID
decl_stmt|;
name|lastDocID
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
comment|// if (DEBUG) System.out.println("  write doc=" + doc.docID + " freq=" + doc.termFreq);
if|if
condition|(
name|doc
operator|.
name|termFreq
operator|==
literal|1
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|doc
operator|.
name|termFreq
condition|;
name|posIDX
operator|++
control|)
block|{
specifier|final
name|Position
name|pos
init|=
name|pending
index|[
name|pendingIDX
operator|++
index|]
decl_stmt|;
assert|assert
name|pos
operator|.
name|docID
operator|==
name|doc
operator|.
name|docID
assert|;
specifier|final
name|int
name|posDelta
init|=
name|pos
operator|.
name|pos
operator|-
name|lastPos
decl_stmt|;
name|lastPos
operator|=
name|pos
operator|.
name|pos
expr_stmt|;
comment|// if (DEBUG) System.out.println("    write pos=" + pos.pos);
specifier|final
name|int
name|payloadLength
init|=
name|pos
operator|.
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|pos
operator|.
name|payload
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|posDelta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|posDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|posDelta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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
condition|)
block|{
comment|//System.out.println("write=" + pos.startOffset + "," + pos.endOffset);
name|int
name|offsetDelta
init|=
name|pos
operator|.
name|startOffset
operator|-
name|lastOffset
decl_stmt|;
name|int
name|offsetLength
init|=
name|pos
operator|.
name|endOffset
operator|-
name|pos
operator|.
name|startOffset
decl_stmt|;
if|if
condition|(
name|offsetLength
operator|!=
name|lastOffsetLength
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|lastOffset
operator|=
name|pos
operator|.
name|startOffset
expr_stmt|;
name|lastOffsetLength
operator|=
name|offsetLength
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
assert|assert
name|storePayloads
assert|;
name|buffer
operator|.
name|writeBytes
argument_list|(
name|pos
operator|.
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|pos
operator|.
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
condition|)
block|{
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|pendingCount
condition|;
name|posIDX
operator|++
control|)
block|{
specifier|final
name|Position
name|doc
init|=
name|pending
index|[
name|posIDX
index|]
decl_stmt|;
specifier|final
name|int
name|delta
init|=
name|doc
operator|.
name|docID
operator|-
name|lastDocID
decl_stmt|;
assert|assert
name|doc
operator|.
name|termFreq
operator|!=
literal|0
assert|;
if|if
condition|(
name|doc
operator|.
name|termFreq
operator|==
literal|1
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|pendingCount
condition|;
name|posIDX
operator|++
control|)
block|{
specifier|final
name|Position
name|doc
init|=
name|pending
index|[
name|posIDX
index|]
decl_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|docID
operator|-
name|lastDocID
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
block|}
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|buffer
operator|.
name|getFilePointer
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|pendingTerms
operator|.
name|add
argument_list|(
operator|new
name|PendingTerm
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|pendingCount
operator|=
literal|0
expr_stmt|;
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
name|wrappedPostingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flushTermsBlock
specifier|public
name|void
name|flushTermsBlock
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("PW: flushTermsBlock start=" + start + " count=" + count + " pendingTerms.size()=" + pendingTerms.size());
name|int
name|wrappedCount
init|=
literal|0
decl_stmt|;
assert|assert
name|buffer
operator|.
name|getFilePointer
argument_list|()
operator|==
literal|0
assert|;
assert|assert
name|start
operator|>=
name|count
assert|;
specifier|final
name|int
name|limit
init|=
name|pendingTerms
operator|.
name|size
argument_list|()
operator|-
name|start
operator|+
name|count
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|pendingTerms
operator|.
name|size
argument_list|()
operator|-
name|start
init|;
name|idx
operator|<
name|limit
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|PendingTerm
name|term
init|=
name|pendingTerms
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|wrappedCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeBytes
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|term
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
name|termsOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|int
operator|)
name|buffer
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|termsOut
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// TDOO: this could be somewhat costly since
comment|// pendingTerms.size() could be biggish?
name|int
name|futureWrappedCount
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|limit2
init|=
name|pendingTerms
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|limit
init|;
name|idx
operator|<
name|limit2
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|pendingTerms
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|==
literal|null
condition|)
block|{
name|futureWrappedCount
operator|++
expr_stmt|;
block|}
block|}
comment|// Remove the terms we just wrote:
name|pendingTerms
operator|.
name|subList
argument_list|(
name|pendingTerms
operator|.
name|size
argument_list|()
operator|-
name|start
argument_list|,
name|limit
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// if (DEBUG) System.out.println("PW:   len=" + buffer.getFilePointer() + " fp=" + termsOut.getFilePointer() + " futureWrappedCount=" + futureWrappedCount + " wrappedCount=" + wrappedCount);
comment|// TODO: can we avoid calling this if all terms
comment|// were inlined...?  Eg for a "primary key" field, the
comment|// wrapped codec is never invoked...
name|wrappedPostingsWriter
operator|.
name|flushTermsBlock
argument_list|(
name|futureWrappedCount
operator|+
name|wrappedCount
argument_list|,
name|wrappedCount
argument_list|)
expr_stmt|;
block|}
comment|// Pushes pending positions to the wrapped codec
DECL|method|push
specifier|private
name|void
name|push
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("PW now push @ " + pendingCount + " wrapped=" + wrappedPostingsWriter);
assert|assert
name|pendingCount
operator|==
name|pending
operator|.
name|length
assert|;
name|wrappedPostingsWriter
operator|.
name|startTerm
argument_list|()
expr_stmt|;
comment|// Flush all buffered docs
if|if
condition|(
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
condition|)
block|{
name|Position
name|doc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Position
name|pos
range|:
name|pending
control|)
block|{
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
name|pos
expr_stmt|;
comment|// if (DEBUG) System.out.println("PW: wrapped.startDoc docID=" + doc.docID + " tf=" + doc.termFreq);
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|doc
operator|.
name|docID
argument_list|,
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doc
operator|.
name|docID
operator|!=
name|pos
operator|.
name|docID
condition|)
block|{
assert|assert
name|pos
operator|.
name|docID
operator|>
name|doc
operator|.
name|docID
assert|;
comment|// if (DEBUG) System.out.println("PW: wrapped.finishDoc");
name|wrappedPostingsWriter
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
name|doc
operator|=
name|pos
expr_stmt|;
comment|// if (DEBUG) System.out.println("PW: wrapped.startDoc docID=" + doc.docID + " tf=" + doc.termFreq);
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|doc
operator|.
name|docID
argument_list|,
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
comment|// if (DEBUG) System.out.println("PW:   wrapped.addPos pos=" + pos.pos);
name|wrappedPostingsWriter
operator|.
name|addPosition
argument_list|(
name|pos
operator|.
name|pos
argument_list|,
name|pos
operator|.
name|payload
argument_list|,
name|pos
operator|.
name|startOffset
argument_list|,
name|pos
operator|.
name|endOffset
argument_list|)
expr_stmt|;
block|}
comment|//wrappedPostingsWriter.finishDoc();
block|}
else|else
block|{
for|for
control|(
name|Position
name|doc
range|:
name|pending
control|)
block|{
name|wrappedPostingsWriter
operator|.
name|startDoc
argument_list|(
name|doc
operator|.
name|docID
argument_list|,
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|?
literal|0
else|:
name|doc
operator|.
name|termFreq
argument_list|)
expr_stmt|;
block|}
block|}
name|pendingCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
end_class

end_unit

