begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|TermVectorsReader
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|Fields
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
name|SegmentInfo
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
name|store
operator|.
name|AlreadyClosedException
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
name|BufferedChecksumIndexInput
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
name|ChecksumIndexInput
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
name|Bits
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
name|CharsRef
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
name|RamUsageEstimator
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
name|StringHelper
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
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextTermVectorsWriter
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Reads plain-text term vectors.  *<p>  *<b>FOR RECREATIONAL USE ONLY</b>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextTermVectorsReader
specifier|public
class|class
name|SimpleTextTermVectorsReader
extends|extends
name|TermVectorsReader
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|SimpleTextTermVectorsReader
operator|.
name|class
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|BytesRef
operator|.
name|class
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|CharsRef
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsets
specifier|private
name|long
name|offsets
index|[]
decl_stmt|;
comment|/* docid -> offset in .vec file */
DECL|field|in
specifier|private
name|IndexInput
name|in
decl_stmt|;
DECL|field|scratch
specifier|private
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|scratchUTF16
specifier|private
name|CharsRefBuilder
name|scratchUTF16
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|method|SimpleTextTermVectorsReader
specifier|public
name|SimpleTextTermVectorsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|in
operator|=
name|directory
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|VECTORS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
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
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{}
comment|// ensure we throw our original exception
block|}
block|}
name|readIndex
argument_list|(
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// used by clone
DECL|method|SimpleTextTermVectorsReader
name|SimpleTextTermVectorsReader
parameter_list|(
name|long
name|offsets
index|[]
parameter_list|,
name|IndexInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|// we don't actually write a .tvx-like index, instead we read the
comment|// vectors file in entirety up-front and save the offsets
comment|// so we can seek to the data later.
DECL|method|readIndex
specifier|private
name|void
name|readIndex
parameter_list|(
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|ChecksumIndexInput
name|input
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|offsets
operator|=
operator|new
name|long
index|[
name|maxDoc
index|]
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|scratch
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|END
argument_list|)
condition|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|DOC
argument_list|)
condition|)
block|{
name|offsets
index|[
name|upto
index|]
operator|=
name|input
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
name|SimpleTextUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|)
expr_stmt|;
assert|assert
name|upto
operator|==
name|offsets
operator|.
name|length
assert|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Fields
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SimpleTVTerms
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offsets
index|[
name|doc
index|]
argument_list|)
expr_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|NUMFIELDS
argument_list|)
assert|;
name|int
name|numFields
init|=
name|parseIntAt
argument_list|(
name|NUMFIELDS
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|numFields
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
comment|// no vectors for this doc
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|FIELD
argument_list|)
assert|;
comment|// skip fieldNumber:
name|parseIntAt
argument_list|(
name|FIELD
operator|.
name|length
argument_list|)
expr_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|FIELDNAME
argument_list|)
assert|;
name|String
name|fieldName
init|=
name|readString
argument_list|(
name|FIELDNAME
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|FIELDPOSITIONS
argument_list|)
assert|;
name|boolean
name|positions
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|FIELDPOSITIONS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|FIELDOFFSETS
argument_list|)
assert|;
name|boolean
name|offsets
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|FIELDOFFSETS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|FIELDPAYLOADS
argument_list|)
assert|;
name|boolean
name|payloads
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|FIELDPAYLOADS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|FIELDTERMCOUNT
argument_list|)
assert|;
name|int
name|termCount
init|=
name|parseIntAt
argument_list|(
name|FIELDTERMCOUNT
operator|.
name|length
argument_list|)
decl_stmt|;
name|SimpleTVTerms
name|terms
init|=
operator|new
name|SimpleTVTerms
argument_list|(
name|offsets
argument_list|,
name|positions
argument_list|,
name|payloads
argument_list|)
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|BytesRefBuilder
name|term
init|=
operator|new
name|BytesRefBuilder
argument_list|()
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
name|termCount
condition|;
name|j
operator|++
control|)
block|{
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|TERMTEXT
argument_list|)
assert|;
name|int
name|termLength
init|=
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|TERMTEXT
operator|.
name|length
decl_stmt|;
name|term
operator|.
name|grow
argument_list|(
name|termLength
argument_list|)
expr_stmt|;
name|term
operator|.
name|setLength
argument_list|(
name|termLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|TERMTEXT
operator|.
name|length
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
name|SimpleTVPostings
name|postings
init|=
operator|new
name|SimpleTVPostings
argument_list|()
decl_stmt|;
name|terms
operator|.
name|terms
operator|.
name|put
argument_list|(
name|term
operator|.
name|toBytesRef
argument_list|()
argument_list|,
name|postings
argument_list|)
expr_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|TERMFREQ
argument_list|)
assert|;
name|postings
operator|.
name|freq
operator|=
name|parseIntAt
argument_list|(
name|TERMFREQ
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|positions
operator|||
name|offsets
condition|)
block|{
if|if
condition|(
name|positions
condition|)
block|{
name|postings
operator|.
name|positions
operator|=
operator|new
name|int
index|[
name|postings
operator|.
name|freq
index|]
expr_stmt|;
if|if
condition|(
name|payloads
condition|)
block|{
name|postings
operator|.
name|payloads
operator|=
operator|new
name|BytesRef
index|[
name|postings
operator|.
name|freq
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|offsets
condition|)
block|{
name|postings
operator|.
name|startOffsets
operator|=
operator|new
name|int
index|[
name|postings
operator|.
name|freq
index|]
expr_stmt|;
name|postings
operator|.
name|endOffsets
operator|=
operator|new
name|int
index|[
name|postings
operator|.
name|freq
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|postings
operator|.
name|freq
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|positions
condition|)
block|{
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|POSITION
argument_list|)
assert|;
name|postings
operator|.
name|positions
index|[
name|k
index|]
operator|=
name|parseIntAt
argument_list|(
name|POSITION
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|payloads
condition|)
block|{
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|PAYLOAD
argument_list|)
assert|;
if|if
condition|(
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|PAYLOAD
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|postings
operator|.
name|payloads
index|[
name|k
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|byte
name|payloadBytes
index|[]
init|=
operator|new
name|byte
index|[
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|PAYLOAD
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|PAYLOAD
operator|.
name|length
argument_list|,
name|payloadBytes
argument_list|,
literal|0
argument_list|,
name|payloadBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|postings
operator|.
name|payloads
index|[
name|k
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|payloadBytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|offsets
condition|)
block|{
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|STARTOFFSET
argument_list|)
assert|;
name|postings
operator|.
name|startOffsets
index|[
name|k
index|]
operator|=
name|parseIntAt
argument_list|(
name|STARTOFFSET
operator|.
name|length
argument_list|)
expr_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|ENDOFFSET
argument_list|)
assert|;
name|postings
operator|.
name|endOffsets
index|[
name|k
index|]
operator|=
name|parseIntAt
argument_list|(
name|ENDOFFSET
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
operator|new
name|SimpleTVFields
argument_list|(
name|fields
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|TermVectorsReader
name|clone
parameter_list|()
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this TermVectorsReader is closed"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SimpleTextTermVectorsReader
argument_list|(
name|offsets
argument_list|,
name|in
operator|.
name|clone
argument_list|()
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
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|=
literal|null
expr_stmt|;
name|offsets
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|readLine
specifier|private
name|void
name|readLine
parameter_list|()
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|parseIntAt
specifier|private
name|int
name|parseIntAt
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|scratchUTF16
operator|.
name|copyUTF8Bytes
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|offset
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|offset
argument_list|)
expr_stmt|;
return|return
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
name|scratchUTF16
operator|.
name|chars
argument_list|()
argument_list|,
literal|0
argument_list|,
name|scratchUTF16
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readString
specifier|private
name|String
name|readString
parameter_list|(
name|int
name|offset
parameter_list|,
name|BytesRefBuilder
name|scratch
parameter_list|)
block|{
name|scratchUTF16
operator|.
name|copyUTF8Bytes
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|offset
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|offset
argument_list|)
expr_stmt|;
return|return
name|scratchUTF16
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|SimpleTVFields
specifier|private
class|class
name|SimpleTVFields
extends|extends
name|Fields
block|{
DECL|field|fields
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SimpleTVTerms
argument_list|>
name|fields
decl_stmt|;
DECL|method|SimpleTVFields
name|SimpleTVFields
parameter_list|(
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SimpleTVTerms
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fields
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|class|SimpleTVTerms
specifier|private
specifier|static
class|class
name|SimpleTVTerms
extends|extends
name|Terms
block|{
DECL|field|terms
specifier|final
name|SortedMap
argument_list|<
name|BytesRef
argument_list|,
name|SimpleTVPostings
argument_list|>
name|terms
decl_stmt|;
DECL|field|hasOffsets
specifier|final
name|boolean
name|hasOffsets
decl_stmt|;
DECL|field|hasPositions
specifier|final
name|boolean
name|hasPositions
decl_stmt|;
DECL|field|hasPayloads
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|method|SimpleTVTerms
name|SimpleTVTerms
parameter_list|(
name|boolean
name|hasOffsets
parameter_list|,
name|boolean
name|hasPositions
parameter_list|,
name|boolean
name|hasPayloads
parameter_list|)
block|{
name|this
operator|.
name|hasOffsets
operator|=
name|hasOffsets
expr_stmt|;
name|this
operator|.
name|hasPositions
operator|=
name|hasPositions
expr_stmt|;
name|this
operator|.
name|hasPayloads
operator|=
name|hasPayloads
expr_stmt|;
name|terms
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: reuse
return|return
operator|new
name|SimpleTVTermsEnum
argument_list|(
name|terms
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|terms
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|terms
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|hasFreqs
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
name|hasOffsets
return|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
name|hasPositions
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
block|}
DECL|class|SimpleTVPostings
specifier|private
specifier|static
class|class
name|SimpleTVPostings
block|{
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|positions
specifier|private
name|int
name|positions
index|[]
decl_stmt|;
DECL|field|startOffsets
specifier|private
name|int
name|startOffsets
index|[]
decl_stmt|;
DECL|field|endOffsets
specifier|private
name|int
name|endOffsets
index|[]
decl_stmt|;
DECL|field|payloads
specifier|private
name|BytesRef
name|payloads
index|[]
decl_stmt|;
block|}
DECL|class|SimpleTVTermsEnum
specifier|private
specifier|static
class|class
name|SimpleTVTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|terms
name|SortedMap
argument_list|<
name|BytesRef
argument_list|,
name|SimpleTVPostings
argument_list|>
name|terms
decl_stmt|;
DECL|field|iterator
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|SimpleTextTermVectorsReader
operator|.
name|SimpleTVPostings
argument_list|>
argument_list|>
name|iterator
decl_stmt|;
DECL|field|current
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|SimpleTextTermVectorsReader
operator|.
name|SimpleTVPostings
argument_list|>
name|current
decl_stmt|;
DECL|method|SimpleTVTermsEnum
name|SimpleTVTermsEnum
parameter_list|(
name|SortedMap
argument_list|<
name|BytesRef
argument_list|,
name|SimpleTVPostings
argument_list|>
name|terms
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|terms
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|iterator
operator|=
name|terms
operator|.
name|tailMap
argument_list|(
name|text
argument_list|)
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
else|else
block|{
return|return
name|next
argument_list|()
operator|.
name|equals
argument_list|(
name|text
argument_list|)
condition|?
name|SeekStatus
operator|.
name|FOUND
else|:
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
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
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|current
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|getKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
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
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|getValue
argument_list|()
operator|.
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: reuse
name|SimpleTVDocsEnum
name|e
init|=
operator|new
name|SimpleTVDocsEnum
argument_list|()
decl_stmt|;
name|e
operator|.
name|reset
argument_list|(
name|liveDocs
argument_list|,
operator|(
name|flags
operator|&
name|DocsEnum
operator|.
name|FLAG_FREQS
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
name|current
operator|.
name|getValue
argument_list|()
operator|.
name|freq
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTVPostings
name|postings
init|=
name|current
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|postings
operator|.
name|positions
operator|==
literal|null
operator|&&
name|postings
operator|.
name|startOffsets
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// TODO: reuse
name|SimpleTVDocsAndPositionsEnum
name|e
init|=
operator|new
name|SimpleTVDocsAndPositionsEnum
argument_list|()
decl_stmt|;
name|e
operator|.
name|reset
argument_list|(
name|liveDocs
argument_list|,
name|postings
operator|.
name|positions
argument_list|,
name|postings
operator|.
name|startOffsets
argument_list|,
name|postings
operator|.
name|endOffsets
argument_list|,
name|postings
operator|.
name|payloads
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
comment|// note: these two enum classes are exactly like the Default impl...
DECL|class|SimpleTVDocsEnum
specifier|private
specifier|static
class|class
name|SimpleTVDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|didNext
specifier|private
name|boolean
name|didNext
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|liveDocs
specifier|private
name|Bits
name|liveDocs
decl_stmt|;
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
assert|assert
name|freq
operator|!=
operator|-
literal|1
assert|;
return|return
name|freq
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
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
if|if
condition|(
operator|!
name|didNext
operator|&&
operator|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
condition|)
block|{
name|didNext
operator|=
literal|true
expr_stmt|;
return|return
operator|(
name|doc
operator|=
literal|0
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|doc
operator|=
name|NO_MORE_DOCS
operator|)
return|;
block|}
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
name|slowAdvance
argument_list|(
name|target
argument_list|)
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|freq
operator|=
name|freq
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|didNext
operator|=
literal|false
expr_stmt|;
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
literal|1
return|;
block|}
block|}
DECL|class|SimpleTVDocsAndPositionsEnum
specifier|private
specifier|static
class|class
name|SimpleTVDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|didNext
specifier|private
name|boolean
name|didNext
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|nextPos
specifier|private
name|int
name|nextPos
decl_stmt|;
DECL|field|liveDocs
specifier|private
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|positions
specifier|private
name|int
index|[]
name|positions
decl_stmt|;
DECL|field|payloads
specifier|private
name|BytesRef
index|[]
name|payloads
decl_stmt|;
DECL|field|startOffsets
specifier|private
name|int
index|[]
name|startOffsets
decl_stmt|;
DECL|field|endOffsets
specifier|private
name|int
index|[]
name|endOffsets
decl_stmt|;
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
if|if
condition|(
name|positions
operator|!=
literal|null
condition|)
block|{
return|return
name|positions
operator|.
name|length
return|;
block|}
else|else
block|{
assert|assert
name|startOffsets
operator|!=
literal|null
assert|;
return|return
name|startOffsets
operator|.
name|length
return|;
block|}
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
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
if|if
condition|(
operator|!
name|didNext
operator|&&
operator|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
condition|)
block|{
name|didNext
operator|=
literal|true
expr_stmt|;
return|return
operator|(
name|doc
operator|=
literal|0
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|doc
operator|=
name|NO_MORE_DOCS
operator|)
return|;
block|}
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
name|slowAdvance
argument_list|(
name|target
argument_list|)
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|int
index|[]
name|positions
parameter_list|,
name|int
index|[]
name|startOffsets
parameter_list|,
name|int
index|[]
name|endOffsets
parameter_list|,
name|BytesRef
name|payloads
index|[]
parameter_list|)
block|{
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
name|this
operator|.
name|startOffsets
operator|=
name|startOffsets
expr_stmt|;
name|this
operator|.
name|endOffsets
operator|=
name|endOffsets
expr_stmt|;
name|this
operator|.
name|payloads
operator|=
name|payloads
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|didNext
operator|=
literal|false
expr_stmt|;
name|nextPos
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
return|return
name|payloads
operator|==
literal|null
condition|?
literal|null
else|:
name|payloads
index|[
name|nextPos
operator|-
literal|1
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
block|{
assert|assert
operator|(
name|positions
operator|!=
literal|null
operator|&&
name|nextPos
operator|<
name|positions
operator|.
name|length
operator|)
operator|||
name|startOffsets
operator|!=
literal|null
operator|&&
name|nextPos
operator|<
name|startOffsets
operator|.
name|length
assert|;
if|if
condition|(
name|positions
operator|!=
literal|null
condition|)
block|{
return|return
name|positions
index|[
name|nextPos
operator|++
index|]
return|;
block|}
else|else
block|{
name|nextPos
operator|++
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
block|{
if|if
condition|(
name|startOffsets
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|startOffsets
index|[
name|nextPos
operator|-
literal|1
index|]
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
block|{
if|if
condition|(
name|endOffsets
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|endOffsets
index|[
name|nextPos
operator|-
literal|1
index|]
return|;
block|}
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
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|offsets
argument_list|)
return|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

