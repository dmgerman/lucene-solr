begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Base class for specific Bytes Reader/Writer implementations */
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
name|Collection
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|values
operator|.
name|IndexDocValues
operator|.
name|SortedSource
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
name|values
operator|.
name|IndexDocValues
operator|.
name|Source
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
name|values
operator|.
name|IndexDocValues
operator|.
name|SourceEnum
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
name|util
operator|.
name|AttributeSource
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
name|PagedBytes
import|;
end_import

begin_comment
comment|/**  * Provides concrete Writer/Reader implementations for<tt>byte[]</tt> value per  * document. There are 6 package-private default implementations of this, for  * all combinations of {@link Mode#DEREF}/{@link Mode#STRAIGHT}/  * {@link Mode#SORTED} x fixed-length/variable-length.  *   *<p>  * NOTE: Currently the total amount of byte[] data stored (across a single  * segment) cannot exceed 2GB.  *</p>  *<p>  * NOTE: Each byte[] must be<= 32768 bytes in length  *</p>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|Bytes
specifier|public
specifier|final
class|class
name|Bytes
block|{
comment|// TODO - add bulk copy where possible
DECL|method|Bytes
specifier|private
name|Bytes
parameter_list|()
block|{
comment|/* don't instantiate! */
block|}
comment|/**    * Defines the {@link Writer}s store mode. The writer will either store the    * bytes sequentially ({@link #STRAIGHT}, dereferenced ({@link #DEREF}) or    * sorted ({@link #SORTED})    *     * @lucene.experimental    */
DECL|enum|Mode
specifier|public
specifier|static
enum|enum
name|Mode
block|{
comment|/**      * Mode for sequentially stored bytes      */
DECL|enum constant|STRAIGHT
name|STRAIGHT
block|,
comment|/**      * Mode for dereferenced stored bytes      */
DECL|enum constant|DEREF
name|DEREF
block|,
comment|/**      * Mode for sorted stored bytes      */
DECL|enum constant|SORTED
name|SORTED
block|}
empty_stmt|;
comment|/**    * Creates a new<tt>byte[]</tt> {@link Writer} instances for the given    * directory.    *     * @param dir    *          the directory to write the values to    * @param id    *          the id used to create a unique file name. Usually composed out of    *          the segment name and a unique id per segment.    * @param mode    *          the writers store mode    * @param comp    *          a {@link BytesRef} comparator - only used with {@link Mode#SORTED}    * @param fixedSize    *<code>true</code> if all bytes subsequently passed to the    *          {@link Writer} will have the same length    * @param bytesUsed    *          an {@link AtomicLong} instance to track the used bytes within the    *          {@link Writer}. A call to {@link Writer#finish(int)} will release    *          all internally used resources and frees the memeory tracking    *          reference.    * @param context     * @return a new {@link Writer} instance    * @throws IOException    *           if the files for the writer can not be created.    */
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Mode
name|mode
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|boolean
name|fixedSize
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO -- i shouldn't have to specify fixed? can
comment|// track itself& do the write thing at write time?
if|if
condition|(
name|comp
operator|==
literal|null
condition|)
block|{
name|comp
operator|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fixedSize
condition|)
block|{
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|STRAIGHT
condition|)
block|{
return|return
operator|new
name|FixedStraightBytesImpl
operator|.
name|Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|DEREF
condition|)
block|{
return|return
operator|new
name|FixedDerefBytesImpl
operator|.
name|Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|SORTED
condition|)
block|{
return|return
operator|new
name|FixedSortedBytesImpl
operator|.
name|Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|STRAIGHT
condition|)
block|{
return|return
operator|new
name|VarStraightBytesImpl
operator|.
name|Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|DEREF
condition|)
block|{
return|return
operator|new
name|VarDerefBytesImpl
operator|.
name|Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|SORTED
condition|)
block|{
return|return
operator|new
name|VarSortedBytesImpl
operator|.
name|Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|""
argument_list|)
throw|;
block|}
comment|/**    * Creates a new {@link IndexDocValues} instance that provides either memory    * resident or iterative access to a per-document stored<tt>byte[]</tt>    * value. The returned {@link IndexDocValues} instance will be initialized without    * consuming a significant amount of memory.    *     * @param dir    *          the directory to load the {@link IndexDocValues} from.    * @param id    *          the file ID in the {@link Directory} to load the values from.    * @param mode    *          the mode used to store the values    * @param fixedSize    *<code>true</code> iff the values are stored with fixed-size,    *          otherwise<code>false</code>    * @param maxDoc    *          the number of document values stored for the given ID    * @param sortComparator byte comparator used by sorted variants    * @return an initialized {@link IndexDocValues} instance.    * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|getValues
specifier|public
specifier|static
name|IndexDocValues
name|getValues
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Mode
name|mode
parameter_list|,
name|boolean
name|fixedSize
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|sortComparator
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO -- I can peek @ header to determing fixed/mode?
if|if
condition|(
name|fixedSize
condition|)
block|{
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|STRAIGHT
condition|)
block|{
return|return
operator|new
name|FixedStraightBytesImpl
operator|.
name|Reader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|DEREF
condition|)
block|{
return|return
operator|new
name|FixedDerefBytesImpl
operator|.
name|Reader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|SORTED
condition|)
block|{
return|return
operator|new
name|FixedSortedBytesImpl
operator|.
name|Reader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|STRAIGHT
condition|)
block|{
return|return
operator|new
name|VarStraightBytesImpl
operator|.
name|Reader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|DEREF
condition|)
block|{
return|return
operator|new
name|VarDerefBytesImpl
operator|.
name|Reader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|SORTED
condition|)
block|{
return|return
operator|new
name|VarSortedBytesImpl
operator|.
name|Reader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|sortComparator
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal Mode: "
operator|+
name|mode
argument_list|)
throw|;
block|}
comment|// TODO open up this API?
DECL|class|BytesBaseSource
specifier|static
specifier|abstract
class|class
name|BytesBaseSource
extends|extends
name|Source
block|{
DECL|field|pagedBytes
specifier|private
specifier|final
name|PagedBytes
name|pagedBytes
decl_stmt|;
DECL|field|datIn
specifier|protected
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|idxIn
specifier|protected
specifier|final
name|IndexInput
name|idxIn
decl_stmt|;
DECL|field|PAGED_BYTES_BITS
specifier|protected
specifier|final
specifier|static
name|int
name|PAGED_BYTES_BITS
init|=
literal|15
decl_stmt|;
DECL|field|data
specifier|protected
specifier|final
name|PagedBytes
operator|.
name|Reader
name|data
decl_stmt|;
DECL|field|totalLengthInBytes
specifier|protected
specifier|final
name|long
name|totalLengthInBytes
decl_stmt|;
DECL|method|BytesBaseSource
specifier|protected
name|BytesBaseSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|PagedBytes
name|pagedBytes
parameter_list|,
name|long
name|bytesToRead
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bytesToRead
operator|<=
name|datIn
operator|.
name|length
argument_list|()
operator|:
literal|" file size is less than the expected size diff: "
operator|+
operator|(
name|bytesToRead
operator|-
name|datIn
operator|.
name|length
argument_list|()
operator|)
operator|+
literal|" pos: "
operator|+
name|datIn
operator|.
name|getFilePointer
argument_list|()
assert|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
name|this
operator|.
name|totalLengthInBytes
operator|=
name|bytesToRead
expr_stmt|;
name|this
operator|.
name|pagedBytes
operator|=
name|pagedBytes
expr_stmt|;
name|this
operator|.
name|pagedBytes
operator|.
name|copy
argument_list|(
name|datIn
argument_list|,
name|bytesToRead
argument_list|)
expr_stmt|;
name|data
operator|=
name|pagedBytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|idxIn
operator|=
name|idxIn
expr_stmt|;
block|}
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
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close data
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|datIn
operator|!=
literal|null
condition|)
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|idxIn
operator|!=
literal|null
condition|)
block|{
comment|// if straight - no index needed
name|idxIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Returns one greater than the largest possible document number.      */
DECL|method|maxDoc
specifier|protected
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SourceEnum
argument_list|(
name|attrSource
argument_list|,
name|type
argument_list|()
argument_list|,
name|this
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
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
if|if
condition|(
name|target
operator|>=
name|numDocs
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
while|while
condition|(
name|source
operator|.
name|getBytes
argument_list|(
name|target
argument_list|,
name|bytesRef
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|++
name|target
operator|>=
name|numDocs
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
return|return
name|pos
operator|=
name|target
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|BytesBaseSortedSource
specifier|static
specifier|abstract
class|class
name|BytesBaseSortedSource
extends|extends
name|SortedSource
block|{
DECL|field|datIn
specifier|protected
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|idxIn
specifier|protected
specifier|final
name|IndexInput
name|idxIn
decl_stmt|;
DECL|field|defaultValue
specifier|protected
specifier|final
name|BytesRef
name|defaultValue
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|PAGED_BYTES_BITS
specifier|protected
specifier|final
specifier|static
name|int
name|PAGED_BYTES_BITS
init|=
literal|15
decl_stmt|;
DECL|field|pagedBytes
specifier|private
specifier|final
name|PagedBytes
name|pagedBytes
decl_stmt|;
DECL|field|data
specifier|protected
specifier|final
name|PagedBytes
operator|.
name|Reader
name|data
decl_stmt|;
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|method|BytesBaseSortedSource
specifier|protected
name|BytesBaseSortedSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|PagedBytes
name|pagedBytes
parameter_list|,
name|long
name|bytesToRead
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bytesToRead
operator|<=
name|datIn
operator|.
name|length
argument_list|()
operator|:
literal|" file size is less than the expected size diff: "
operator|+
operator|(
name|bytesToRead
operator|-
name|datIn
operator|.
name|length
argument_list|()
operator|)
operator|+
literal|" pos: "
operator|+
name|datIn
operator|.
name|getFilePointer
argument_list|()
assert|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
name|this
operator|.
name|pagedBytes
operator|=
name|pagedBytes
expr_stmt|;
name|this
operator|.
name|pagedBytes
operator|.
name|copy
argument_list|(
name|datIn
argument_list|,
name|bytesToRead
argument_list|)
expr_stmt|;
name|data
operator|=
name|pagedBytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|idxIn
operator|=
name|idxIn
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
operator|==
literal|null
condition|?
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
else|:
name|comp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
assert|assert
name|ord
operator|>=
literal|0
assert|;
return|return
name|deref
argument_list|(
name|ord
argument_list|,
name|bytesRef
argument_list|)
return|;
block|}
DECL|method|closeIndexInput
specifier|protected
name|void
name|closeIndexInput
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|datIn
operator|!=
literal|null
condition|)
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|idxIn
operator|!=
literal|null
condition|)
block|{
comment|// if straight
name|idxIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Returns the largest doc id + 1 in this doc values source      */
DECL|method|maxDoc
specifier|protected
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
comment|/**      * Copies the value for the given ord to the given {@link BytesRef} and      * returns it.      */
DECL|method|deref
specifier|protected
specifier|abstract
name|BytesRef
name|deref
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
function_decl|;
DECL|method|binarySearch
specifier|protected
name|int
name|binarySearch
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|,
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
name|int
name|mid
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
expr_stmt|;
name|deref
argument_list|(
name|mid
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
specifier|final
name|int
name|cmp
init|=
name|comp
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
block|}
block|}
assert|assert
name|comp
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
operator|!=
literal|0
assert|;
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SourceEnum
argument_list|(
name|attrSource
argument_list|,
name|type
argument_list|()
argument_list|,
name|this
argument_list|,
name|maxDoc
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
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
if|if
condition|(
name|target
operator|>=
name|numDocs
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
while|while
condition|(
name|source
operator|.
name|getBytes
argument_list|(
name|target
argument_list|,
name|bytesRef
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|++
name|target
operator|>=
name|numDocs
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
return|return
name|pos
operator|=
name|target
return|;
block|}
block|}
return|;
block|}
block|}
comment|// TODO: open up this API?!
DECL|class|BytesWriterBase
specifier|static
specifier|abstract
class|class
name|BytesWriterBase
extends|extends
name|Writer
block|{
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|idxOut
specifier|private
name|IndexOutput
name|idxOut
decl_stmt|;
DECL|field|datOut
specifier|private
name|IndexOutput
name|datOut
decl_stmt|;
DECL|field|bytesRef
specifier|protected
name|BytesRef
name|bytesRef
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|codecName
specifier|private
specifier|final
name|String
name|codecName
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|method|BytesWriterBase
specifier|protected
name|BytesWriterBase
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|codecName
parameter_list|,
name|int
name|version
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|codecName
operator|=
name|codecName
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|getDataOut
specifier|protected
name|IndexOutput
name|getDataOut
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|datOut
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|DATA_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|datOut
argument_list|,
name|codecName
argument_list|,
name|version
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|datOut
return|;
block|}
DECL|method|getIndexOut
specifier|protected
name|IndexOutput
name|getIndexOut
parameter_list|()
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
if|if
condition|(
name|idxOut
operator|==
literal|null
condition|)
block|{
name|idxOut
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|idxOut
argument_list|,
name|codecName
argument_list|,
name|version
argument_list|)
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
name|idxOut
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|idxOut
return|;
block|}
comment|/**      * Must be called only with increasing docIDs. It's OK for some docIDs to be      * skipped; they will be filled with 0 bytes.      */
annotation|@
name|Override
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|PerDocFieldValues
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|ref
decl_stmt|;
if|if
condition|(
operator|(
name|ref
operator|=
name|docValues
operator|.
name|getBytes
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextEnum
specifier|protected
name|void
name|setNextEnum
parameter_list|(
name|ValuesEnum
name|valuesEnum
parameter_list|)
block|{
name|bytesRef
operator|=
name|valuesEnum
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|datOut
operator|!=
literal|null
assert|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|idxOut
operator|!=
literal|null
condition|)
block|{
comment|// called after flush - so this must be initialized
comment|// if needed or present
specifier|final
name|String
name|idxFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|idxFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Opens all necessary files, but does not read any data in until you call    * {@link #load}.    */
DECL|class|BytesReaderBase
specifier|static
specifier|abstract
class|class
name|BytesReaderBase
extends|extends
name|IndexDocValues
block|{
DECL|field|idxIn
specifier|protected
specifier|final
name|IndexInput
name|idxIn
decl_stmt|;
DECL|field|datIn
specifier|protected
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|version
specifier|protected
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|id
specifier|protected
specifier|final
name|String
name|id
decl_stmt|;
DECL|method|BytesReaderBase
specifier|protected
name|BytesReaderBase
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|codecName
parameter_list|,
name|int
name|maxVersion
parameter_list|,
name|boolean
name|doIndex
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|datIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|datIn
argument_list|,
name|codecName
argument_list|,
name|maxVersion
argument_list|,
name|maxVersion
argument_list|)
expr_stmt|;
if|if
condition|(
name|doIndex
condition|)
block|{
name|idxIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
specifier|final
name|int
name|version2
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|idxIn
argument_list|,
name|codecName
argument_list|,
name|maxVersion
argument_list|,
name|maxVersion
argument_list|)
decl_stmt|;
assert|assert
name|version
operator|==
name|version2
assert|;
block|}
else|else
block|{
name|idxIn
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
name|closeInternal
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * clones and returns the data {@link IndexInput}      */
DECL|method|cloneData
specifier|protected
specifier|final
name|IndexInput
name|cloneData
parameter_list|()
block|{
assert|assert
name|datIn
operator|!=
literal|null
assert|;
return|return
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**      * clones and returns the indexing {@link IndexInput}      */
DECL|method|cloneIndex
specifier|protected
specifier|final
name|IndexInput
name|cloneIndex
parameter_list|()
block|{
assert|assert
name|idxIn
operator|!=
literal|null
assert|;
return|return
operator|(
name|IndexInput
operator|)
name|idxIn
operator|.
name|clone
argument_list|()
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|closeInternal
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|closeInternal
specifier|private
name|void
name|closeInternal
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|idxIn
operator|!=
literal|null
condition|)
block|{
name|idxIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

