begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|EOFException
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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|TrackingDirectoryWrapper
import|;
end_import

begin_comment
comment|/**  * On-disk sorting of byte arrays. Each byte array (entry) is a composed of the following  * fields:  *<ul>  *<li>(two bytes) length of the following byte array,  *<li>exactly the above count of bytes for the sequence to be sorted.  *</ul>  *   * @see #sort(String)  * @lucene.experimental  * @lucene.internal  */
end_comment

begin_class
DECL|class|OfflineSorter
specifier|public
class|class
name|OfflineSorter
block|{
comment|/** Convenience constant for megabytes */
DECL|field|MB
specifier|public
specifier|final
specifier|static
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/** Convenience constant for gigabytes */
DECL|field|GB
specifier|public
specifier|final
specifier|static
name|long
name|GB
init|=
name|MB
operator|*
literal|1024
decl_stmt|;
comment|/**    * Minimum recommended buffer size for sorting.    */
DECL|field|MIN_BUFFER_SIZE_MB
specifier|public
specifier|final
specifier|static
name|long
name|MIN_BUFFER_SIZE_MB
init|=
literal|32
decl_stmt|;
comment|/**    * Absolute minimum required buffer size for sorting.    */
DECL|field|ABSOLUTE_MIN_SORT_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|long
name|ABSOLUTE_MIN_SORT_BUFFER_SIZE
init|=
name|MB
operator|/
literal|2
decl_stmt|;
DECL|field|MIN_BUFFER_SIZE_MSG
specifier|private
specifier|static
specifier|final
name|String
name|MIN_BUFFER_SIZE_MSG
init|=
literal|"At least 0.5MB RAM buffer is needed"
decl_stmt|;
comment|/**    * Maximum number of temporary files before doing an intermediate merge.    */
DECL|field|MAX_TEMPFILES
specifier|public
specifier|final
specifier|static
name|int
name|MAX_TEMPFILES
init|=
literal|10
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|tempFileNamePrefix
specifier|private
specifier|final
name|String
name|tempFileNamePrefix
decl_stmt|;
comment|/**     * A bit more descriptive unit for constructors.    *     * @see #automatic()    * @see #megabytes(long)    */
DECL|class|BufferSize
specifier|public
specifier|static
specifier|final
class|class
name|BufferSize
block|{
DECL|field|bytes
specifier|final
name|int
name|bytes
decl_stmt|;
DECL|method|BufferSize
specifier|private
name|BufferSize
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Buffer too large for Java ("
operator|+
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
name|MB
operator|)
operator|+
literal|"mb max): "
operator|+
name|bytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytes
operator|<
name|ABSOLUTE_MIN_SORT_BUFFER_SIZE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MIN_BUFFER_SIZE_MSG
operator|+
literal|": "
operator|+
name|bytes
argument_list|)
throw|;
block|}
name|this
operator|.
name|bytes
operator|=
operator|(
name|int
operator|)
name|bytes
expr_stmt|;
block|}
comment|/**      * Creates a {@link BufferSize} in MB. The given       * values must be&gt; 0 and&lt; 2048.      */
DECL|method|megabytes
specifier|public
specifier|static
name|BufferSize
name|megabytes
parameter_list|(
name|long
name|mb
parameter_list|)
block|{
return|return
operator|new
name|BufferSize
argument_list|(
name|mb
operator|*
name|MB
argument_list|)
return|;
block|}
comment|/**       * Approximately half of the currently available free heap, but no less      * than {@link #ABSOLUTE_MIN_SORT_BUFFER_SIZE}. However if current heap allocation       * is insufficient or if there is a large portion of unallocated heap-space available       * for sorting consult with max allowed heap size.       */
DECL|method|automatic
specifier|public
specifier|static
name|BufferSize
name|automatic
parameter_list|()
block|{
name|Runtime
name|rt
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
comment|// take sizes in "conservative" order
specifier|final
name|long
name|max
init|=
name|rt
operator|.
name|maxMemory
argument_list|()
decl_stmt|;
comment|// max allocated
specifier|final
name|long
name|total
init|=
name|rt
operator|.
name|totalMemory
argument_list|()
decl_stmt|;
comment|// currently allocated
specifier|final
name|long
name|free
init|=
name|rt
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
comment|// unused portion of currently allocated
specifier|final
name|long
name|totalAvailableBytes
init|=
name|max
operator|-
name|total
operator|+
name|free
decl_stmt|;
comment|// by free mem (attempting to not grow the heap for this)
name|long
name|sortBufferByteSize
init|=
name|free
operator|/
literal|2
decl_stmt|;
specifier|final
name|long
name|minBufferSizeBytes
init|=
name|MIN_BUFFER_SIZE_MB
operator|*
name|MB
decl_stmt|;
if|if
condition|(
name|sortBufferByteSize
argument_list|<
name|minBufferSizeBytes
operator|||
name|totalAvailableBytes
argument_list|>
literal|10
operator|*
name|minBufferSizeBytes
condition|)
block|{
comment|// lets see if we need/should to grow the heap
if|if
condition|(
name|totalAvailableBytes
operator|/
literal|2
operator|>
name|minBufferSizeBytes
condition|)
block|{
comment|// there is enough mem for a reasonable buffer
name|sortBufferByteSize
operator|=
name|totalAvailableBytes
operator|/
literal|2
expr_stmt|;
comment|// grow the heap
block|}
else|else
block|{
comment|//heap seems smallish lets be conservative fall back to the free/2
name|sortBufferByteSize
operator|=
name|Math
operator|.
name|max
argument_list|(
name|ABSOLUTE_MIN_SORT_BUFFER_SIZE
argument_list|,
name|sortBufferByteSize
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|BufferSize
argument_list|(
name|Math
operator|.
name|min
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sortBufferByteSize
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Sort info (debugging mostly).    */
DECL|class|SortInfo
specifier|public
class|class
name|SortInfo
block|{
comment|/** number of temporary files created when merging partitions */
DECL|field|tempMergeFiles
specifier|public
name|int
name|tempMergeFiles
decl_stmt|;
comment|/** number of partition merges */
DECL|field|mergeRounds
specifier|public
name|int
name|mergeRounds
decl_stmt|;
comment|/** number of lines of data read */
DECL|field|lineCount
specifier|public
name|int
name|lineCount
decl_stmt|;
comment|/** time spent merging sorted partitions (in milliseconds) */
DECL|field|mergeTime
specifier|public
name|long
name|mergeTime
decl_stmt|;
comment|/** time spent sorting data (in milliseconds) */
DECL|field|sortTime
specifier|public
name|long
name|sortTime
decl_stmt|;
comment|/** total time spent (in milliseconds) */
DECL|field|totalTime
specifier|public
name|long
name|totalTime
decl_stmt|;
comment|/** time spent in i/o read (in milliseconds) */
DECL|field|readTime
specifier|public
name|long
name|readTime
decl_stmt|;
comment|/** read buffer size (in bytes) */
DECL|field|bufferSize
specifier|public
specifier|final
name|long
name|bufferSize
init|=
name|ramBufferSize
operator|.
name|bytes
decl_stmt|;
comment|/** create a new SortInfo (with empty statistics) for debugging */
DECL|method|SortInfo
specifier|public
name|SortInfo
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"time=%.2f sec. total (%.2f reading, %.2f sorting, %.2f merging), lines=%d, temp files=%d, merges=%d, soft ram limit=%.2f MB"
argument_list|,
name|totalTime
operator|/
literal|1000.0d
argument_list|,
name|readTime
operator|/
literal|1000.0d
argument_list|,
name|sortTime
operator|/
literal|1000.0d
argument_list|,
name|mergeTime
operator|/
literal|1000.0d
argument_list|,
name|lineCount
argument_list|,
name|tempMergeFiles
argument_list|,
name|mergeRounds
argument_list|,
operator|(
name|double
operator|)
name|bufferSize
operator|/
name|MB
argument_list|)
return|;
block|}
block|}
DECL|field|ramBufferSize
specifier|private
specifier|final
name|BufferSize
name|ramBufferSize
decl_stmt|;
DECL|field|bufferBytesUsed
specifier|private
specifier|final
name|Counter
name|bufferBytesUsed
init|=
name|Counter
operator|.
name|newCounter
argument_list|()
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|BytesRefArray
name|buffer
init|=
operator|new
name|BytesRefArray
argument_list|(
name|bufferBytesUsed
argument_list|)
decl_stmt|;
DECL|field|sortInfo
name|SortInfo
name|sortInfo
decl_stmt|;
DECL|field|maxTempFiles
specifier|private
name|int
name|maxTempFiles
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
comment|/** Default comparator: sorts in binary (codepoint) order */
DECL|field|DEFAULT_COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|DEFAULT_COMPARATOR
init|=
name|Comparator
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
comment|/**    * Defaults constructor.    *     * @see BufferSize#automatic()    */
DECL|method|OfflineSorter
specifier|public
name|OfflineSorter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|tempFileNamePrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|tempFileNamePrefix
argument_list|,
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|automatic
argument_list|()
argument_list|,
name|MAX_TEMPFILES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Defaults constructor with a custom comparator.    *     * @see BufferSize#automatic()    */
DECL|method|OfflineSorter
specifier|public
name|OfflineSorter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|tempFileNamePrefix
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
argument_list|(
name|dir
argument_list|,
name|tempFileNamePrefix
argument_list|,
name|comparator
argument_list|,
name|BufferSize
operator|.
name|automatic
argument_list|()
argument_list|,
name|MAX_TEMPFILES
argument_list|)
expr_stmt|;
block|}
comment|/**    * All-details constructor.    */
DECL|method|OfflineSorter
specifier|public
name|OfflineSorter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|tempFileNamePrefix
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|,
name|BufferSize
name|ramBufferSize
parameter_list|,
name|int
name|maxTempfiles
parameter_list|)
block|{
if|if
condition|(
name|ramBufferSize
operator|.
name|bytes
operator|<
name|ABSOLUTE_MIN_SORT_BUFFER_SIZE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MIN_BUFFER_SIZE_MSG
operator|+
literal|": "
operator|+
name|ramBufferSize
operator|.
name|bytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxTempfiles
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTempFiles must be>= 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|ramBufferSize
operator|=
name|ramBufferSize
expr_stmt|;
name|this
operator|.
name|maxTempFiles
operator|=
name|maxTempfiles
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|tempFileNamePrefix
operator|=
name|tempFileNamePrefix
expr_stmt|;
block|}
comment|/** Returns the {@link Directory} we use to create temp files. */
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
comment|/** Returns the temp file name prefix passed to {@link Directory#createTempOutput} to generate temporary files. */
DECL|method|getTempFileNamePrefix
specifier|public
name|String
name|getTempFileNamePrefix
parameter_list|()
block|{
return|return
name|tempFileNamePrefix
return|;
block|}
comment|/**     * Sort input to a new temp file, returning its name.    */
DECL|method|sort
specifier|public
name|String
name|sort
parameter_list|(
name|String
name|inputFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|sortInfo
operator|=
operator|new
name|SortInfo
argument_list|()
expr_stmt|;
name|sortInfo
operator|.
name|totalTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|segments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
index|[]
name|levelCounts
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
comment|// So we can remove any partially written temp files on exception:
name|TrackingDirectoryWrapper
name|trackingDir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|ByteSequencesReader
name|is
init|=
name|getReader
argument_list|(
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|inputFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
argument_list|,
name|inputFileName
argument_list|)
init|)
block|{
name|int
name|lineCount
decl_stmt|;
while|while
condition|(
operator|(
name|lineCount
operator|=
name|readPartition
argument_list|(
name|is
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|segments
operator|.
name|add
argument_list|(
name|sortPartition
argument_list|(
name|trackingDir
argument_list|)
argument_list|)
expr_stmt|;
name|sortInfo
operator|.
name|tempMergeFiles
operator|++
expr_stmt|;
name|sortInfo
operator|.
name|lineCount
operator|+=
name|lineCount
expr_stmt|;
name|levelCounts
index|[
literal|0
index|]
operator|++
expr_stmt|;
comment|// Handle intermediate merges; we need a while loop to "cascade" the merge when necessary:
name|int
name|mergeLevel
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|levelCounts
index|[
name|mergeLevel
index|]
operator|==
name|maxTempFiles
condition|)
block|{
name|mergePartitions
argument_list|(
name|trackingDir
argument_list|,
name|segments
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeLevel
operator|+
literal|2
operator|>
name|levelCounts
operator|.
name|length
condition|)
block|{
name|levelCounts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|levelCounts
argument_list|,
name|mergeLevel
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
name|levelCounts
index|[
name|mergeLevel
operator|+
literal|1
index|]
operator|++
expr_stmt|;
name|levelCounts
index|[
name|mergeLevel
index|]
operator|=
literal|0
expr_stmt|;
name|mergeLevel
operator|++
expr_stmt|;
block|}
block|}
comment|// TODO: we shouldn't have to do this?  Can't we return a merged reader to
comment|// the caller, who often consumes the result just once, instead?
comment|// Merge all partitions down to 1 (basically a forceMerge(1)):
while|while
condition|(
name|segments
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|mergePartitions
argument_list|(
name|trackingDir
argument_list|,
name|segments
argument_list|)
expr_stmt|;
block|}
name|String
name|result
decl_stmt|;
if|if
condition|(
name|segments
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
init|(
name|IndexOutput
name|out
init|=
name|trackingDir
operator|.
name|createTempOutput
argument_list|(
name|tempFileNamePrefix
argument_list|,
literal|"sort"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
comment|// Write empty file footer
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|result
operator|=
name|out
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|segments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// We should be explicitly removing all intermediate files ourselves unless there is an exception:
assert|assert
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
operator|.
name|contains
argument_list|(
name|result
argument_list|)
assert|;
name|sortInfo
operator|.
name|totalTime
operator|=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|sortInfo
operator|.
name|totalTime
operator|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|is
operator|.
name|in
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|result
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|trackingDir
argument_list|,
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Sort a single partition in-memory. */
DECL|method|sortPartition
specifier|protected
name|String
name|sortPartition
parameter_list|(
name|TrackingDirectoryWrapper
name|trackingDir
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|IndexOutput
name|tempFile
init|=
name|trackingDir
operator|.
name|createTempOutput
argument_list|(
name|tempFileNamePrefix
argument_list|,
literal|"sort"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|;
name|ByteSequencesWriter
name|out
operator|=
name|getWriter
argument_list|(
name|tempFile
argument_list|)
init|;
init|)
block|{
name|BytesRef
name|spare
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|BytesRefIterator
name|iter
init|=
name|buffer
operator|.
name|iterator
argument_list|(
name|comparator
argument_list|)
decl_stmt|;
name|sortInfo
operator|.
name|sortTime
operator|+=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
assert|assert
name|spare
operator|.
name|length
operator|<=
name|Short
operator|.
name|MAX_VALUE
assert|;
name|out
operator|.
name|write
argument_list|(
name|spare
argument_list|)
expr_stmt|;
block|}
comment|// Clean up the buffer for the next partition.
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|out
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
name|tempFile
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
comment|/** Called on exception, to check whether the checksum is also corrupt in this source, and add that     *  information (checksum matched or didn't) as a suppressed exception. */
DECL|method|verifyChecksum
specifier|private
name|void
name|verifyChecksum
parameter_list|(
name|Throwable
name|priorException
parameter_list|,
name|ByteSequencesReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ChecksumIndexInput
name|in
init|=
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|reader
operator|.
name|name
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|in
argument_list|,
name|priorException
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Merge the most recent {@code maxTempFile} partitions into a new partition. */
DECL|method|mergePartitions
name|void
name|mergePartitions
parameter_list|(
name|Directory
name|trackingDir
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|segments
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|segmentsToMerge
decl_stmt|;
if|if
condition|(
name|segments
operator|.
name|size
argument_list|()
operator|>
name|maxTempFiles
condition|)
block|{
name|segmentsToMerge
operator|=
name|segments
operator|.
name|subList
argument_list|(
name|segments
operator|.
name|size
argument_list|()
operator|-
name|maxTempFiles
argument_list|,
name|segments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|segmentsToMerge
operator|=
name|segments
expr_stmt|;
block|}
name|PriorityQueue
argument_list|<
name|FileAndTop
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|FileAndTop
argument_list|>
argument_list|(
name|segmentsToMerge
operator|.
name|size
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FileAndTop
name|a
parameter_list|,
name|FileAndTop
name|b
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|a
operator|.
name|current
operator|.
name|get
argument_list|()
argument_list|,
name|b
operator|.
name|current
operator|.
name|get
argument_list|()
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
decl_stmt|;
name|ByteSequencesReader
index|[]
name|streams
init|=
operator|new
name|ByteSequencesReader
index|[
name|segmentsToMerge
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|String
name|newSegmentName
init|=
literal|null
decl_stmt|;
try|try
init|(
name|ByteSequencesWriter
name|writer
init|=
name|getWriter
argument_list|(
name|trackingDir
operator|.
name|createTempOutput
argument_list|(
name|tempFileNamePrefix
argument_list|,
literal|"sort"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
argument_list|)
init|)
block|{
name|newSegmentName
operator|=
name|writer
operator|.
name|out
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|// Open streams and read the top for each file
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentsToMerge
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|streams
index|[
name|i
index|]
operator|=
name|getReader
argument_list|(
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|segmentsToMerge
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
argument_list|,
name|segmentsToMerge
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
block|{
name|result
operator|=
name|streams
index|[
name|i
index|]
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|verifyChecksum
argument_list|(
name|t
argument_list|,
name|streams
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
assert|assert
name|result
assert|;
name|queue
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|FileAndTop
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Unix utility sort() uses ordered array of files to pick the next line from, updating
comment|// it as it reads new lines. The PQ used here is a more elegant solution and has
comment|// a nicer theoretical complexity bound :) The entire sorting process is I/O bound anyway
comment|// so it shouldn't make much of a difference (didn't check).
name|FileAndTop
name|top
decl_stmt|;
while|while
condition|(
operator|(
name|top
operator|=
name|queue
operator|.
name|top
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|top
operator|.
name|current
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|top
operator|.
name|current
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
block|{
name|result
operator|=
name|streams
index|[
name|top
operator|.
name|fd
index|]
operator|.
name|read
argument_list|(
name|top
operator|.
name|current
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|verifyChecksum
argument_list|(
name|t
argument_list|,
name|streams
index|[
name|top
operator|.
name|fd
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
condition|)
block|{
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|writer
operator|.
name|out
argument_list|)
expr_stmt|;
for|for
control|(
name|ByteSequencesReader
name|reader
range|:
name|streams
control|)
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|reader
operator|.
name|in
argument_list|)
expr_stmt|;
block|}
name|sortInfo
operator|.
name|mergeTime
operator|+=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
name|sortInfo
operator|.
name|mergeRounds
operator|++
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|deleteFiles
argument_list|(
name|trackingDir
argument_list|,
name|segmentsToMerge
argument_list|)
expr_stmt|;
name|segmentsToMerge
operator|.
name|clear
argument_list|()
expr_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|newSegmentName
argument_list|)
expr_stmt|;
name|sortInfo
operator|.
name|tempMergeFiles
operator|++
expr_stmt|;
block|}
comment|/** Read in a single partition of data */
DECL|method|readPartition
name|int
name|readPartition
parameter_list|(
name|ByteSequencesReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
block|{
name|result
operator|=
name|reader
operator|.
name|read
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|verifyChecksum
argument_list|(
name|t
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|false
condition|)
block|{
break|break;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// Account for the created objects.
comment|// (buffer slots do not account to buffer size.)
if|if
condition|(
name|bufferBytesUsed
operator|.
name|get
argument_list|()
operator|>
name|ramBufferSize
operator|.
name|bytes
condition|)
block|{
break|break;
block|}
block|}
name|sortInfo
operator|.
name|readTime
operator|+=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
return|return
name|buffer
operator|.
name|size
argument_list|()
return|;
block|}
DECL|class|FileAndTop
specifier|static
class|class
name|FileAndTop
block|{
DECL|field|fd
specifier|final
name|int
name|fd
decl_stmt|;
DECL|field|current
specifier|final
name|BytesRefBuilder
name|current
decl_stmt|;
DECL|method|FileAndTop
name|FileAndTop
parameter_list|(
name|int
name|fd
parameter_list|,
name|BytesRefBuilder
name|firstLine
parameter_list|)
block|{
name|this
operator|.
name|fd
operator|=
name|fd
expr_stmt|;
name|this
operator|.
name|current
operator|=
name|firstLine
expr_stmt|;
block|}
block|}
comment|/** Subclasses can override to change how byte sequences are written to disk. */
DECL|method|getWriter
specifier|protected
name|ByteSequencesWriter
name|getWriter
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteSequencesWriter
argument_list|(
name|out
argument_list|)
return|;
block|}
comment|/** Subclasses can override to change how byte sequences are read from disk. */
DECL|method|getReader
specifier|protected
name|ByteSequencesReader
name|getReader
parameter_list|(
name|ChecksumIndexInput
name|in
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteSequencesReader
argument_list|(
name|in
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**    * Utility class to emit length-prefixed byte[] entries to an output stream for sorting.    * Complementary to {@link ByteSequencesReader}.  You must use {@link CodecUtil#writeFooter}    * to write a footer at the end of the input file.    */
DECL|class|ByteSequencesWriter
specifier|public
specifier|static
class|class
name|ByteSequencesWriter
implements|implements
name|Closeable
block|{
DECL|field|out
specifier|protected
specifier|final
name|IndexOutput
name|out
decl_stmt|;
comment|/** Constructs a ByteSequencesWriter to the provided DataOutput */
DECL|method|ByteSequencesWriter
specifier|public
name|ByteSequencesWriter
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**      * Writes a BytesRef.      * @see #write(byte[], int, int)      */
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|BytesRef
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|ref
operator|!=
literal|null
assert|;
name|write
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes a byte array.      * @see #write(byte[], int, int)      */
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes a byte array.      *<p>      * The length is written as a<code>short</code>, followed      * by the bytes.      */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bytes
operator|!=
literal|null
assert|;
assert|assert
name|off
operator|>=
literal|0
operator|&&
name|off
operator|+
name|len
operator|<=
name|bytes
operator|.
name|length
assert|;
assert|assert
name|len
operator|>=
literal|0
assert|;
if|if
condition|(
name|len
operator|>
name|Short
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"len must be<= "
operator|+
name|Short
operator|.
name|MAX_VALUE
operator|+
literal|"; got "
operator|+
name|len
argument_list|)
throw|;
block|}
name|out
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**      * Closes the provided {@link IndexOutput}.      */
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
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Utility class to read length-prefixed byte[] entries from an input.    * Complementary to {@link ByteSequencesWriter}.    */
DECL|class|ByteSequencesReader
specifier|public
specifier|static
class|class
name|ByteSequencesReader
implements|implements
name|Closeable
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|in
specifier|protected
specifier|final
name|ChecksumIndexInput
name|in
decl_stmt|;
DECL|field|end
specifier|protected
specifier|final
name|long
name|end
decl_stmt|;
comment|/** Constructs a ByteSequencesReader from the provided IndexInput */
DECL|method|ByteSequencesReader
specifier|public
name|ByteSequencesReader
parameter_list|(
name|ChecksumIndexInput
name|in
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|end
operator|=
name|in
operator|.
name|length
argument_list|()
operator|-
name|CodecUtil
operator|.
name|footerLength
argument_list|()
expr_stmt|;
block|}
comment|/**      * Reads the next entry into the provided {@link BytesRef}. The internal      * storage is resized if needed.      *       * @return Returns<code>false</code> if EOF occurred when trying to read      * the header of the next sequence. Returns<code>true</code> otherwise.      * @throws EOFException if the file ends before the full sequence is read.      */
DECL|method|read
specifier|public
name|boolean
name|read
parameter_list|(
name|BytesRefBuilder
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|>=
name|end
condition|)
block|{
return|return
literal|false
return|;
block|}
name|short
name|length
init|=
name|in
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|ref
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|ref
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Closes the provided {@link IndexInput}.      */
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the comparator in use to sort entries */
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
block|}
end_class

end_unit

