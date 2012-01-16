begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.preflexrw
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|preflexrw
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|FieldsConsumer
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
name|PostingsConsumer
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
name|codecs
operator|.
name|TermsConsumer
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
name|lucene3x
operator|.
name|Lucene3xPostingsFormat
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
name|lucene3x
operator|.
name|TermInfo
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
name|lucene40
operator|.
name|Lucene40SkipListWriter
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
name|CorruptIndexException
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
name|index
operator|.
name|IndexFileNames
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
name|SegmentWriteState
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
name|IOUtils
import|;
end_import

begin_class
DECL|class|PreFlexFieldsWriter
class|class
name|PreFlexFieldsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|termsOut
specifier|private
specifier|final
name|TermInfosWriter
name|termsOut
decl_stmt|;
DECL|field|freqOut
specifier|private
specifier|final
name|IndexOutput
name|freqOut
decl_stmt|;
DECL|field|proxOut
specifier|private
specifier|final
name|IndexOutput
name|proxOut
decl_stmt|;
DECL|field|skipListWriter
specifier|private
specifier|final
name|Lucene40SkipListWriter
name|skipListWriter
decl_stmt|;
DECL|field|totalNumDocs
specifier|private
specifier|final
name|int
name|totalNumDocs
decl_stmt|;
DECL|method|PreFlexFieldsWriter
specifier|public
name|PreFlexFieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|termsOut
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|termIndexInterval
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|freqFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene3xPostingsFormat
operator|.
name|FREQ_EXTENSION
argument_list|)
decl_stmt|;
name|freqOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|freqFile
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|totalNumDocs
operator|=
name|state
operator|.
name|numDocs
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|termsOut
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasProx
argument_list|()
condition|)
block|{
specifier|final
name|String
name|proxFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene3xPostingsFormat
operator|.
name|PROX_EXTENSION
argument_list|)
decl_stmt|;
name|proxOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|proxFile
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxOut
operator|=
literal|null
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|termsOut
argument_list|,
name|freqOut
argument_list|)
expr_stmt|;
block|}
block|}
name|skipListWriter
operator|=
operator|new
name|Lucene40SkipListWriter
argument_list|(
name|termsOut
operator|.
name|skipInterval
argument_list|,
name|termsOut
operator|.
name|maxSkipLevels
argument_list|,
name|totalNumDocs
argument_list|,
name|freqOut
argument_list|,
name|proxOut
argument_list|)
expr_stmt|;
comment|//System.out.println("\nw start seg=" + segment);
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|number
operator|!=
operator|-
literal|1
assert|;
if|if
condition|(
name|field
operator|.
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec cannot index offsets"
argument_list|)
throw|;
block|}
comment|//System.out.println("w field=" + field.name + " storePayload=" + field.storePayloads + " number=" + field.number);
return|return
operator|new
name|PreFlexTermsWriter
argument_list|(
name|field
argument_list|)
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
name|IOUtils
operator|.
name|close
argument_list|(
name|termsOut
argument_list|,
name|freqOut
argument_list|,
name|proxOut
argument_list|)
expr_stmt|;
block|}
DECL|class|PreFlexTermsWriter
specifier|private
class|class
name|PreFlexTermsWriter
extends|extends
name|TermsConsumer
block|{
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|omitTF
specifier|private
specifier|final
name|boolean
name|omitTF
decl_stmt|;
DECL|field|storePayloads
specifier|private
specifier|final
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|termInfo
specifier|private
specifier|final
name|TermInfo
name|termInfo
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
DECL|field|postingsWriter
specifier|private
specifier|final
name|PostingsWriter
name|postingsWriter
init|=
operator|new
name|PostingsWriter
argument_list|()
decl_stmt|;
DECL|method|PreFlexTermsWriter
specifier|public
name|PreFlexTermsWriter
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
name|omitTF
operator|=
name|fieldInfo
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
block|}
DECL|class|PostingsWriter
specifier|private
class|class
name|PostingsWriter
extends|extends
name|PostingsConsumer
block|{
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
decl_stmt|;
DECL|field|lastPayloadLength
specifier|private
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|lastPosition
specifier|private
name|int
name|lastPosition
decl_stmt|;
DECL|field|df
specifier|private
name|int
name|df
decl_stmt|;
DECL|method|reset
specifier|public
name|PostingsWriter
name|reset
parameter_list|()
block|{
name|df
operator|=
literal|0
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|lastPayloadLength
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|this
return|;
block|}
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
comment|//System.out.println("    w doc=" + docID);
specifier|final
name|int
name|delta
init|=
name|docID
operator|-
name|lastDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
operator|(
name|df
operator|>
literal|0
operator|&&
name|delta
operator|<=
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"docs out of order ("
operator|+
name|docID
operator|+
literal|"<= "
operator|+
name|lastDocID
operator|+
literal|" )"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|++
name|df
operator|%
name|termsOut
operator|.
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
name|skipListWriter
operator|.
name|setSkipData
argument_list|(
name|lastDocID
argument_list|,
name|storePayloads
argument_list|,
name|lastPayloadLength
argument_list|)
expr_stmt|;
name|skipListWriter
operator|.
name|bufferSkip
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
assert|assert
name|docID
operator|<
name|totalNumDocs
operator|:
literal|"docID="
operator|+
name|docID
operator|+
literal|" totalNumDocs="
operator|+
name|totalNumDocs
assert|;
if|if
condition|(
name|omitTF
condition|)
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|code
init|=
name|delta
operator|<<
literal|1
decl_stmt|;
if|if
condition|(
name|termDocFreq
operator|==
literal|1
condition|)
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|code
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
block|}
name|lastPosition
operator|=
literal|0
expr_stmt|;
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
assert|assert
name|proxOut
operator|!=
literal|null
assert|;
assert|assert
name|startOffset
operator|==
operator|-
literal|1
assert|;
assert|assert
name|endOffset
operator|==
operator|-
literal|1
assert|;
comment|//System.out.println("      w pos=" + position + " payl=" + payload);
specifier|final
name|int
name|delta
init|=
name|position
operator|-
name|lastPosition
decl_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
specifier|final
name|int
name|payloadLength
init|=
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|payload
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
comment|//System.out.println("        write payload len=" + payloadLength);
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
name|proxOut
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
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
name|proxOut
operator|.
name|writeBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
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
block|{       }
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("  w term=" + text.utf8ToString());
name|skipListWriter
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
name|termInfo
operator|.
name|freqPointer
operator|=
name|freqOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxOut
operator|!=
literal|null
condition|)
block|{
name|termInfo
operator|.
name|proxPointer
operator|=
name|proxOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
return|return
name|postingsWriter
operator|.
name|reset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stats
operator|.
name|docFreq
operator|>
literal|0
condition|)
block|{
name|long
name|skipPointer
init|=
name|skipListWriter
operator|.
name|writeSkip
argument_list|(
name|freqOut
argument_list|)
decl_stmt|;
name|termInfo
operator|.
name|docFreq
operator|=
name|stats
operator|.
name|docFreq
expr_stmt|;
name|termInfo
operator|.
name|skipOffset
operator|=
call|(
name|int
call|)
argument_list|(
name|skipPointer
operator|-
name|termInfo
operator|.
name|freqPointer
argument_list|)
expr_stmt|;
comment|//System.out.println("  w finish term=" + text.utf8ToString() + " fnum=" + fieldInfo.number);
name|termsOut
operator|.
name|add
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|text
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

