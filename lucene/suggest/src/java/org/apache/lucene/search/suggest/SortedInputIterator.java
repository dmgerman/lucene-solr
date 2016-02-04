begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|store
operator|.
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|Directory
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
name|IOContext
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
name|OfflineSorter
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
name|OfflineSorter
operator|.
name|ByteSequencesReader
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
name|OfflineSorter
operator|.
name|ByteSequencesWriter
import|;
end_import

begin_comment
comment|/**  * This wrapper buffers incoming elements and makes sure they are sorted based on given comparator.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SortedInputIterator
specifier|public
class|class
name|SortedInputIterator
implements|implements
name|InputIterator
block|{
DECL|field|source
specifier|private
specifier|final
name|InputIterator
name|source
decl_stmt|;
DECL|field|tempInput
specifier|private
name|IndexOutput
name|tempInput
decl_stmt|;
DECL|field|tempSortedFileName
specifier|private
name|String
name|tempSortedFileName
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|ByteSequencesReader
name|reader
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|field|hasPayloads
specifier|private
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|field|hasContexts
specifier|private
specifier|final
name|boolean
name|hasContexts
decl_stmt|;
DECL|field|tempDir
specifier|private
specifier|final
name|Directory
name|tempDir
decl_stmt|;
DECL|field|tempFileNamePrefix
specifier|private
specifier|final
name|String
name|tempFileNamePrefix
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|weight
specifier|private
name|long
name|weight
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|contexts
specifier|private
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
init|=
literal|null
decl_stmt|;
comment|/**    * Creates a new sorted wrapper, using {@link    * BytesRef#getUTF8SortedAsUnicodeComparator} for    * sorting. */
DECL|method|SortedInputIterator
specifier|public
name|SortedInputIterator
parameter_list|(
name|Directory
name|tempDir
parameter_list|,
name|String
name|tempFileNamePrefix
parameter_list|,
name|InputIterator
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|tempDir
argument_list|,
name|tempFileNamePrefix
argument_list|,
name|source
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new sorted wrapper, sorting by BytesRef    * (ascending) then cost (ascending).    */
DECL|method|SortedInputIterator
specifier|public
name|SortedInputIterator
parameter_list|(
name|Directory
name|tempDir
parameter_list|,
name|String
name|tempFileNamePrefix
parameter_list|,
name|InputIterator
name|source
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|hasPayloads
operator|=
name|source
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|this
operator|.
name|hasContexts
operator|=
name|source
operator|.
name|hasContexts
argument_list|()
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|tempDir
operator|=
name|tempDir
expr_stmt|;
name|this
operator|.
name|tempFileNamePrefix
operator|=
name|tempFileNamePrefix
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|sort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|done
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|read
argument_list|(
name|scratch
argument_list|)
condition|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|scratch
operator|.
name|get
argument_list|()
decl_stmt|;
name|weight
operator|=
name|decode
argument_list|(
name|bytes
argument_list|,
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|payload
operator|=
name|decodePayload
argument_list|(
name|bytes
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasContexts
condition|)
block|{
name|contexts
operator|=
name|decodeContexts
argument_list|(
name|bytes
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|bytes
return|;
block|}
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
name|done
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
if|if
condition|(
name|hasPayloads
condition|)
block|{
return|return
name|payload
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
return|return
name|contexts
return|;
block|}
annotation|@
name|Override
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
name|hasContexts
return|;
block|}
comment|/** Sortes by BytesRef (ascending) then cost (ascending). */
DECL|field|tieBreakByCostComparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|tieBreakByCostComparator
init|=
operator|new
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|BytesRef
name|leftScratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BytesRef
name|rightScratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|left
parameter_list|,
name|BytesRef
name|right
parameter_list|)
block|{
comment|// Make shallow copy in case decode changes the BytesRef:
name|leftScratch
operator|.
name|bytes
operator|=
name|left
operator|.
name|bytes
expr_stmt|;
name|leftScratch
operator|.
name|offset
operator|=
name|left
operator|.
name|offset
expr_stmt|;
name|leftScratch
operator|.
name|length
operator|=
name|left
operator|.
name|length
expr_stmt|;
name|rightScratch
operator|.
name|bytes
operator|=
name|right
operator|.
name|bytes
expr_stmt|;
name|rightScratch
operator|.
name|offset
operator|=
name|right
operator|.
name|offset
expr_stmt|;
name|rightScratch
operator|.
name|length
operator|=
name|right
operator|.
name|length
expr_stmt|;
name|long
name|leftCost
init|=
name|decode
argument_list|(
name|leftScratch
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|long
name|rightCost
init|=
name|decode
argument_list|(
name|rightScratch
argument_list|,
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|decodePayload
argument_list|(
name|leftScratch
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|decodePayload
argument_list|(
name|rightScratch
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasContexts
condition|)
block|{
name|decodeContexts
argument_list|(
name|leftScratch
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|decodeContexts
argument_list|(
name|rightScratch
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|leftScratch
argument_list|,
name|rightScratch
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
return|return
name|Long
operator|.
name|compare
argument_list|(
name|leftCost
argument_list|,
name|rightCost
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|sort
specifier|private
name|ByteSequencesReader
name|sort
parameter_list|()
throws|throws
name|IOException
block|{
name|OfflineSorter
name|sorter
init|=
operator|new
name|OfflineSorter
argument_list|(
name|tempDir
argument_list|,
name|tempFileNamePrefix
argument_list|,
name|tieBreakByCostComparator
argument_list|)
decl_stmt|;
name|tempInput
operator|=
name|tempDir
operator|.
name|createTempOutput
argument_list|(
name|tempFileNamePrefix
argument_list|,
literal|"input"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
specifier|final
name|OfflineSorter
operator|.
name|ByteSequencesWriter
name|writer
init|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesWriter
argument_list|(
name|tempInput
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|BytesRef
name|spare
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|ByteArrayDataOutput
name|output
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|source
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|encode
argument_list|(
name|writer
argument_list|,
name|output
argument_list|,
name|buffer
argument_list|,
name|spare
argument_list|,
name|source
operator|.
name|payload
argument_list|()
argument_list|,
name|source
operator|.
name|contexts
argument_list|()
argument_list|,
name|source
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|tempSortedFileName
operator|=
name|sorter
operator|.
name|sort
argument_list|(
name|tempInput
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ByteSequencesReader
name|reader
init|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesReader
argument_list|(
name|tempDir
operator|.
name|openInput
argument_list|(
name|tempSortedFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|reader
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|close
specifier|private
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|tempDir
argument_list|,
name|tempInput
operator|==
literal|null
condition|?
literal|null
else|:
name|tempInput
operator|.
name|getName
argument_list|()
argument_list|,
name|tempSortedFileName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** encodes an entry (bytes+(contexts)+(payload)+weight) to the provided writer */
DECL|method|encode
specifier|protected
name|void
name|encode
parameter_list|(
name|ByteSequencesWriter
name|writer
parameter_list|,
name|ByteArrayDataOutput
name|output
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|BytesRef
name|spare
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|long
name|weight
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|requiredLength
init|=
name|spare
operator|.
name|length
operator|+
literal|8
operator|+
operator|(
operator|(
name|hasPayloads
operator|)
condition|?
literal|2
operator|+
name|payload
operator|.
name|length
else|:
literal|0
operator|)
decl_stmt|;
if|if
condition|(
name|hasContexts
condition|)
block|{
for|for
control|(
name|BytesRef
name|ctx
range|:
name|contexts
control|)
block|{
name|requiredLength
operator|+=
literal|2
operator|+
name|ctx
operator|.
name|length
expr_stmt|;
block|}
name|requiredLength
operator|+=
literal|2
expr_stmt|;
comment|// for length of contexts
block|}
if|if
condition|(
name|requiredLength
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|requiredLength
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|spare
operator|.
name|bytes
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
name|spare
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasContexts
condition|)
block|{
for|for
control|(
name|BytesRef
name|ctx
range|:
name|contexts
control|)
block|{
name|output
operator|.
name|writeBytes
argument_list|(
name|ctx
operator|.
name|bytes
argument_list|,
name|ctx
operator|.
name|offset
argument_list|,
name|ctx
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|ctx
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|contexts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|output
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
name|output
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|writeLong
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** decodes the weight at the current position */
DECL|method|decode
specifier|protected
name|long
name|decode
parameter_list|(
name|BytesRef
name|scratch
parameter_list|,
name|ByteArrayDataInput
name|tmpInput
parameter_list|)
block|{
name|tmpInput
operator|.
name|reset
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tmpInput
operator|.
name|skipBytes
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|8
argument_list|)
expr_stmt|;
comment|// suggestion
name|scratch
operator|.
name|length
operator|-=
literal|8
expr_stmt|;
comment|// long
return|return
name|tmpInput
operator|.
name|readLong
argument_list|()
return|;
block|}
comment|/** decodes the contexts at the current position */
DECL|method|decodeContexts
specifier|protected
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|decodeContexts
parameter_list|(
name|BytesRef
name|scratch
parameter_list|,
name|ByteArrayDataInput
name|tmpInput
parameter_list|)
block|{
name|tmpInput
operator|.
name|reset
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tmpInput
operator|.
name|skipBytes
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|//skip to context set size
name|short
name|ctxSetSize
init|=
name|tmpInput
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|length
operator|-=
literal|2
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contextSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|short
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ctxSetSize
condition|;
name|i
operator|++
control|)
block|{
name|tmpInput
operator|.
name|setPosition
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|2
argument_list|)
expr_stmt|;
name|short
name|curContextLength
init|=
name|tmpInput
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|length
operator|-=
literal|2
expr_stmt|;
name|tmpInput
operator|.
name|setPosition
argument_list|(
name|scratch
operator|.
name|length
operator|-
name|curContextLength
argument_list|)
expr_stmt|;
name|BytesRef
name|contextSpare
init|=
operator|new
name|BytesRef
argument_list|(
name|curContextLength
argument_list|)
decl_stmt|;
name|tmpInput
operator|.
name|readBytes
argument_list|(
name|contextSpare
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|curContextLength
argument_list|)
expr_stmt|;
name|contextSpare
operator|.
name|length
operator|=
name|curContextLength
expr_stmt|;
name|contextSet
operator|.
name|add
argument_list|(
name|contextSpare
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|length
operator|-=
name|curContextLength
expr_stmt|;
block|}
return|return
name|contextSet
return|;
block|}
comment|/** decodes the payload at the current position */
DECL|method|decodePayload
specifier|protected
name|BytesRef
name|decodePayload
parameter_list|(
name|BytesRef
name|scratch
parameter_list|,
name|ByteArrayDataInput
name|tmpInput
parameter_list|)
block|{
name|tmpInput
operator|.
name|reset
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tmpInput
operator|.
name|skipBytes
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// skip to payload size
name|short
name|payloadLength
init|=
name|tmpInput
operator|.
name|readShort
argument_list|()
decl_stmt|;
comment|// read payload size
name|tmpInput
operator|.
name|setPosition
argument_list|(
name|scratch
operator|.
name|length
operator|-
literal|2
operator|-
name|payloadLength
argument_list|)
expr_stmt|;
comment|// setPosition to start of payload
name|BytesRef
name|payloadScratch
init|=
operator|new
name|BytesRef
argument_list|(
name|payloadLength
argument_list|)
decl_stmt|;
name|tmpInput
operator|.
name|readBytes
argument_list|(
name|payloadScratch
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
comment|// read payload
name|payloadScratch
operator|.
name|length
operator|=
name|payloadLength
expr_stmt|;
name|scratch
operator|.
name|length
operator|-=
literal|2
expr_stmt|;
comment|// payload length info (short)
name|scratch
operator|.
name|length
operator|-=
name|payloadLength
expr_stmt|;
comment|// payload
return|return
name|payloadScratch
return|;
block|}
block|}
end_class

end_unit

