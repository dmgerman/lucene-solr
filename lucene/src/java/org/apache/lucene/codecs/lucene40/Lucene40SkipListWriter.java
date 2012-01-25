begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|Arrays
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
name|codecs
operator|.
name|MultiLevelSkipListWriter
import|;
end_import

begin_comment
comment|/**  * Implements the skip list writer for the default posting list format  * that stores positions and payloads.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene40SkipListWriter
specifier|public
class|class
name|Lucene40SkipListWriter
extends|extends
name|MultiLevelSkipListWriter
block|{
DECL|field|lastSkipDoc
specifier|private
name|int
index|[]
name|lastSkipDoc
decl_stmt|;
DECL|field|lastSkipPayloadLength
specifier|private
name|int
index|[]
name|lastSkipPayloadLength
decl_stmt|;
DECL|field|lastSkipFreqPointer
specifier|private
name|long
index|[]
name|lastSkipFreqPointer
decl_stmt|;
DECL|field|lastSkipProxPointer
specifier|private
name|long
index|[]
name|lastSkipProxPointer
decl_stmt|;
DECL|field|freqOutput
specifier|private
name|IndexOutput
name|freqOutput
decl_stmt|;
DECL|field|proxOutput
specifier|private
name|IndexOutput
name|proxOutput
decl_stmt|;
DECL|field|curDoc
specifier|private
name|int
name|curDoc
decl_stmt|;
DECL|field|curStorePayloads
specifier|private
name|boolean
name|curStorePayloads
decl_stmt|;
DECL|field|curStoreOffsets
specifier|private
name|boolean
name|curStoreOffsets
decl_stmt|;
DECL|field|curPayloadLength
specifier|private
name|int
name|curPayloadLength
decl_stmt|;
DECL|field|curOffsetLength
specifier|private
name|int
name|curOffsetLength
decl_stmt|;
DECL|field|curFreqPointer
specifier|private
name|long
name|curFreqPointer
decl_stmt|;
DECL|field|curProxPointer
specifier|private
name|long
name|curProxPointer
decl_stmt|;
DECL|method|Lucene40SkipListWriter
specifier|public
name|Lucene40SkipListWriter
parameter_list|(
name|int
name|skipInterval
parameter_list|,
name|int
name|numberOfSkipLevels
parameter_list|,
name|int
name|docCount
parameter_list|,
name|IndexOutput
name|freqOutput
parameter_list|,
name|IndexOutput
name|proxOutput
parameter_list|)
block|{
name|super
argument_list|(
name|skipInterval
argument_list|,
name|numberOfSkipLevels
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|freqOutput
operator|=
name|freqOutput
expr_stmt|;
name|this
operator|.
name|proxOutput
operator|=
name|proxOutput
expr_stmt|;
name|lastSkipDoc
operator|=
operator|new
name|int
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|lastSkipPayloadLength
operator|=
operator|new
name|int
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|lastSkipFreqPointer
operator|=
operator|new
name|long
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|lastSkipProxPointer
operator|=
operator|new
name|long
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
block|}
comment|/**    * Sets the values for the current skip data.     */
DECL|method|setSkipData
specifier|public
name|void
name|setSkipData
parameter_list|(
name|int
name|doc
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|int
name|payloadLength
parameter_list|,
name|boolean
name|storeOffsets
parameter_list|,
name|int
name|offsetLength
parameter_list|)
block|{
name|this
operator|.
name|curDoc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|curStorePayloads
operator|=
name|storePayloads
expr_stmt|;
name|this
operator|.
name|curPayloadLength
operator|=
name|payloadLength
expr_stmt|;
name|this
operator|.
name|curStoreOffsets
operator|=
name|storeOffsets
expr_stmt|;
name|this
operator|.
name|curOffsetLength
operator|=
name|offsetLength
expr_stmt|;
name|this
operator|.
name|curFreqPointer
operator|=
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxOutput
operator|!=
literal|null
condition|)
name|this
operator|.
name|curProxPointer
operator|=
name|proxOutput
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resetSkip
specifier|public
name|void
name|resetSkip
parameter_list|()
block|{
name|super
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipDoc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPayloadLength
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// we don't have to write the first length in the skip list
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipFreqPointer
argument_list|,
name|freqOutput
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|proxOutput
operator|!=
literal|null
condition|)
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipProxPointer
argument_list|,
name|proxOutput
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeSkipData
specifier|protected
name|void
name|writeSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexOutput
name|skipBuffer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// To efficiently store payloads in the posting lists we do not store the length of
comment|// every payload. Instead we omit the length for a payload if the previous payload had
comment|// the same length.
comment|// However, in order to support skipping the payload length at every skip point must be known.
comment|// So we use the same length encoding that we use for the posting lists for the skip data as well:
comment|// Case 1: current field does not store payloads
comment|//           SkipDatum                 --> DocSkip, FreqSkip, ProxSkip
comment|//           DocSkip,FreqSkip,ProxSkip --> VInt
comment|//           DocSkip records the document number before every SkipInterval th  document in TermFreqs.
comment|//           Document numbers are represented as differences from the previous value in the sequence.
comment|// Case 2: current field stores payloads
comment|//           SkipDatum                 --> DocSkip, PayloadLength?, FreqSkip,ProxSkip
comment|//           DocSkip,FreqSkip,ProxSkip --> VInt
comment|//           PayloadLength             --> VInt
comment|//         In this case DocSkip/2 is the difference between
comment|//         the current and the previous value. If DocSkip
comment|//         is odd, then a PayloadLength encoded as VInt follows,
comment|//         if DocSkip is even, then it is assumed that the
comment|//         current payload length equals the length at the previous
comment|//         skip point
if|if
condition|(
name|curStorePayloads
condition|)
block|{
name|int
name|delta
init|=
name|curDoc
operator|-
name|lastSkipDoc
index|[
name|level
index|]
decl_stmt|;
if|if
condition|(
name|curPayloadLength
operator|==
name|lastSkipPayloadLength
index|[
name|level
index|]
condition|)
block|{
comment|// the current payload length equals the length at the previous skip point,
comment|// so we don't store the length again
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the payload length is different from the previous one. We shift the DocSkip,
comment|// set the lowest bit and store the current payload length as VInt.
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|*
literal|2
operator|+
literal|1
argument_list|)
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curPayloadLength
argument_list|)
expr_stmt|;
name|lastSkipPayloadLength
index|[
name|level
index|]
operator|=
name|curPayloadLength
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// current field does not store payloads
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curDoc
operator|-
name|lastSkipDoc
index|[
name|level
index|]
argument_list|)
expr_stmt|;
block|}
comment|// TODO: not sure it really helps to shove this somewhere else if its the same as the last skip
if|if
condition|(
name|curStoreOffsets
condition|)
block|{
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curOffsetLength
argument_list|)
expr_stmt|;
block|}
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curFreqPointer
operator|-
name|lastSkipFreqPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curProxPointer
operator|-
name|lastSkipProxPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipDoc
index|[
name|level
index|]
operator|=
name|curDoc
expr_stmt|;
name|lastSkipFreqPointer
index|[
name|level
index|]
operator|=
name|curFreqPointer
expr_stmt|;
name|lastSkipProxPointer
index|[
name|level
index|]
operator|=
name|curProxPointer
expr_stmt|;
block|}
block|}
end_class

end_unit

