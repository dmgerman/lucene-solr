begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|FieldInfos
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
name|store
operator|.
name|IndexInput
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
name|Bits
import|;
end_import

begin_comment
comment|/** @deprecated (4.0)  *  @lucene.experimental */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SegmentTermDocs
class|class
name|SegmentTermDocs
block|{
comment|//protected SegmentReader parent;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|tis
specifier|private
specifier|final
name|TermInfosReader
name|tis
decl_stmt|;
DECL|field|liveDocs
specifier|protected
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|freqStream
specifier|protected
name|IndexInput
name|freqStream
decl_stmt|;
DECL|field|count
specifier|protected
name|int
name|count
decl_stmt|;
DECL|field|df
specifier|protected
name|int
name|df
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
literal|0
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|skipInterval
specifier|private
name|int
name|skipInterval
decl_stmt|;
DECL|field|maxSkipLevels
specifier|private
name|int
name|maxSkipLevels
decl_stmt|;
DECL|field|skipListReader
specifier|private
name|Lucene3xSkipListReader
name|skipListReader
decl_stmt|;
DECL|field|freqBasePointer
specifier|private
name|long
name|freqBasePointer
decl_stmt|;
DECL|field|proxBasePointer
specifier|private
name|long
name|proxBasePointer
decl_stmt|;
DECL|field|skipPointer
specifier|private
name|long
name|skipPointer
decl_stmt|;
DECL|field|haveSkipped
specifier|private
name|boolean
name|haveSkipped
decl_stmt|;
DECL|field|currentFieldStoresPayloads
specifier|protected
name|boolean
name|currentFieldStoresPayloads
decl_stmt|;
DECL|field|indexOptions
specifier|protected
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|method|SegmentTermDocs
specifier|public
name|SegmentTermDocs
parameter_list|(
name|IndexInput
name|freqStream
parameter_list|,
name|TermInfosReader
name|tis
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|this
operator|.
name|freqStream
operator|=
operator|(
name|IndexInput
operator|)
name|freqStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|tis
operator|=
name|tis
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|skipInterval
operator|=
name|tis
operator|.
name|getSkipInterval
argument_list|()
expr_stmt|;
name|maxSkipLevels
operator|=
name|tis
operator|.
name|getMaxSkipLevels
argument_list|()
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|TermInfo
name|ti
init|=
name|tis
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|seek
argument_list|(
name|ti
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|setLiveDocs
specifier|public
name|void
name|setLiveDocs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|)
block|{
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|SegmentTermEnum
name|segmentTermEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|TermInfo
name|ti
decl_stmt|;
name|Term
name|term
decl_stmt|;
comment|// use comparison of fieldinfos to verify that termEnum belongs to the same segment as this SegmentTermDocs
if|if
condition|(
name|segmentTermEnum
operator|.
name|fieldInfos
operator|==
name|fieldInfos
condition|)
block|{
comment|// optimized case
name|term
operator|=
name|segmentTermEnum
operator|.
name|term
argument_list|()
expr_stmt|;
name|ti
operator|=
name|segmentTermEnum
operator|.
name|termInfo
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// punt case
name|term
operator|=
name|segmentTermEnum
operator|.
name|term
argument_list|()
expr_stmt|;
name|ti
operator|=
name|tis
operator|.
name|get
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|seek
argument_list|(
name|ti
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|seek
name|void
name|seek
parameter_list|(
name|TermInfo
name|ti
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|indexOptions
operator|=
operator|(
name|fi
operator|!=
literal|null
operator|)
condition|?
name|fi
operator|.
name|getIndexOptions
argument_list|()
else|:
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
name|currentFieldStoresPayloads
operator|=
operator|(
name|fi
operator|!=
literal|null
operator|)
condition|?
name|fi
operator|.
name|hasPayloads
argument_list|()
else|:
literal|false
expr_stmt|;
if|if
condition|(
name|ti
operator|==
literal|null
condition|)
block|{
name|df
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|df
operator|=
name|ti
operator|.
name|docFreq
expr_stmt|;
name|doc
operator|=
literal|0
expr_stmt|;
name|freqBasePointer
operator|=
name|ti
operator|.
name|freqPointer
expr_stmt|;
name|proxBasePointer
operator|=
name|ti
operator|.
name|proxPointer
expr_stmt|;
name|skipPointer
operator|=
name|freqBasePointer
operator|+
name|ti
operator|.
name|skipOffset
expr_stmt|;
name|freqStream
operator|.
name|seek
argument_list|(
name|freqBasePointer
argument_list|)
expr_stmt|;
name|haveSkipped
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|freqStream
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|skipListReader
operator|!=
literal|null
condition|)
name|skipListReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doc
specifier|public
specifier|final
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
block|{
assert|assert
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
assert|;
return|return
name|freq
return|;
block|}
DECL|method|skippingDoc
specifier|protected
name|void
name|skippingDoc
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|df
condition|)
return|return
literal|false
return|;
specifier|final
name|int
name|docCode
init|=
name|freqStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|doc
operator|+=
name|docCode
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|+=
name|docCode
operator|>>>
literal|1
expr_stmt|;
comment|// shift off low bit
if|if
condition|(
operator|(
name|docCode
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
comment|// if low bit is set
name|freq
operator|=
literal|1
expr_stmt|;
comment|// freq is one
else|else
block|{
name|freq
operator|=
name|freqStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// else read freq
assert|assert
name|freq
operator|!=
literal|1
assert|;
block|}
block|}
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
break|break;
block|}
name|skippingDoc
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Optimized implementation. */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|length
init|=
name|docs
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
return|return
name|readNoTf
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|,
name|length
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|length
operator|&&
name|count
operator|<
name|df
condition|)
block|{
comment|// manually inlined call to next() for speed
specifier|final
name|int
name|docCode
init|=
name|freqStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|doc
operator|+=
name|docCode
operator|>>>
literal|1
expr_stmt|;
comment|// shift off low bit
if|if
condition|(
operator|(
name|docCode
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
comment|// if low bit is set
name|freq
operator|=
literal|1
expr_stmt|;
comment|// freq is one
else|else
name|freq
operator|=
name|freqStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// else read freq
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
name|freq
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
block|}
return|return
name|i
return|;
block|}
block|}
DECL|method|readNoTf
specifier|private
specifier|final
name|int
name|readNoTf
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|length
operator|&&
name|count
operator|<
name|df
condition|)
block|{
comment|// manually inlined call to next() for speed
name|doc
operator|+=
name|freqStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
comment|// Hardware freq to 1 when term freqs were not
comment|// stored in the index
name|freqs
index|[
name|i
index|]
operator|=
literal|1
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
block|}
return|return
name|i
return|;
block|}
comment|/** Overridden by SegmentTermPositions to skip in prox stream. */
DECL|method|skipProx
specifier|protected
name|void
name|skipProx
parameter_list|(
name|long
name|proxPointer
parameter_list|,
name|int
name|payloadLength
parameter_list|)
throws|throws
name|IOException
block|{}
comment|/** Optimized implementation. */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// don't skip if the target is close (within skipInterval docs away)
if|if
condition|(
operator|(
name|target
operator|-
name|skipInterval
operator|)
operator|>=
name|doc
operator|&&
name|df
operator|>=
name|skipInterval
condition|)
block|{
comment|// optimized case
if|if
condition|(
name|skipListReader
operator|==
literal|null
condition|)
name|skipListReader
operator|=
operator|new
name|Lucene3xSkipListReader
argument_list|(
operator|(
name|IndexInput
operator|)
name|freqStream
operator|.
name|clone
argument_list|()
argument_list|,
name|maxSkipLevels
argument_list|,
name|skipInterval
argument_list|)
expr_stmt|;
comment|// lazily clone
if|if
condition|(
operator|!
name|haveSkipped
condition|)
block|{
comment|// lazily initialize skip stream
name|skipListReader
operator|.
name|init
argument_list|(
name|skipPointer
argument_list|,
name|freqBasePointer
argument_list|,
name|proxBasePointer
argument_list|,
name|df
argument_list|,
name|currentFieldStoresPayloads
argument_list|)
expr_stmt|;
name|haveSkipped
operator|=
literal|true
expr_stmt|;
block|}
name|int
name|newCount
init|=
name|skipListReader
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCount
operator|>
name|count
condition|)
block|{
name|freqStream
operator|.
name|seek
argument_list|(
name|skipListReader
operator|.
name|getFreqPointer
argument_list|()
argument_list|)
expr_stmt|;
name|skipProx
argument_list|(
name|skipListReader
operator|.
name|getProxPointer
argument_list|()
argument_list|,
name|skipListReader
operator|.
name|getPayloadLength
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|skipListReader
operator|.
name|getDoc
argument_list|()
expr_stmt|;
name|count
operator|=
name|newCount
expr_stmt|;
block|}
block|}
comment|// done skipping, now just scan
do|do
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
do|while
condition|(
name|target
operator|>
name|doc
condition|)
do|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

