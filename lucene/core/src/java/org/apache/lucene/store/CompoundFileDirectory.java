begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Codec
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|LiveDocsFormat
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|IndexFormatTooOldException
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
name|DataOutput
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|io
operator|.
name|FileNotFoundException
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

begin_comment
comment|/**  * Class for accessing a compound stream.  * This class implements a directory, but is limited to only read operations.  * Directory methods that would normally modify data throw an exception.  *<p>  * All files belonging to a segment have the same name with varying extensions.  * The extensions correspond to the different file formats used by the {@link Codec}.   * When using the Compound File format these files are collapsed into a   * single<tt>.cfs</tt> file (except for the {@link LiveDocsFormat}, with a   * corresponding<tt>.cfe</tt> file indexing its sub-files.  *<p>  * Files:  *<ul>  *<li><tt>.cfs</tt>: An optional "virtual" file consisting of all the other   *    index files for systems that frequently run out of file handles.  *<li><tt>.cfe</tt>: The "virtual" compound file's entry table holding all   *    entries in the corresponding .cfs file.  *</ul>  *<p>Description:</p>  *<ul>  *<li>Compound (.cfs) --&gt; Header, FileData<sup>FileCount</sup></li>  *<li>Compound Entry Table (.cfe) --&gt; Header, FileCount,&lt;FileName,  *       DataOffset, DataLength&gt;<sup>FileCount</sup></li>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>FileCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>DataOffset,DataLength --&gt; {@link DataOutput#writeLong UInt64}</li>  *<li>FileName --&gt; {@link DataOutput#writeString String}</li>  *<li>FileData --&gt; raw file data</li>  *</ul>  *<p>Notes:</p>  *<ul>  *<li>FileCount indicates how many files are contained in this compound file.   *       The entry table that follows has that many entries.   *<li>Each directory entry contains a long pointer to the start of this file's data  *       section, the files length, and a String with that file's name.  *</ul>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompoundFileDirectory
specifier|public
specifier|final
class|class
name|CompoundFileDirectory
extends|extends
name|Directory
block|{
comment|/** Offset/Length for a slice inside of a compound file */
DECL|class|FileEntry
specifier|public
specifier|static
specifier|final
class|class
name|FileEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
block|}
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|readBufferSize
specifier|protected
specifier|final
name|int
name|readBufferSize
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|entries
decl_stmt|;
DECL|field|openForWrite
specifier|private
specifier|final
name|boolean
name|openForWrite
decl_stmt|;
DECL|field|SENTINEL
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|SENTINEL
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|CompoundFileWriter
name|writer
decl_stmt|;
DECL|field|handle
specifier|private
specifier|final
name|IndexInputSlicer
name|handle
decl_stmt|;
comment|/**    * Create a new CompoundFileDirectory.    */
DECL|method|CompoundFileDirectory
specifier|public
name|CompoundFileDirectory
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|boolean
name|openForWrite
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|readBufferSize
operator|=
name|BufferedIndexInput
operator|.
name|bufferSize
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|isOpen
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|openForWrite
operator|=
name|openForWrite
expr_stmt|;
if|if
condition|(
operator|!
name|openForWrite
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|handle
operator|=
name|directory
operator|.
name|createSlicer
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|entries
operator|=
name|readEntries
argument_list|(
name|handle
argument_list|,
name|directory
argument_list|,
name|fileName
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
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|isOpen
operator|=
literal|true
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
operator|!
operator|(
name|directory
operator|instanceof
name|CompoundFileDirectory
operator|)
operator|:
literal|"compound file inside of compound file: "
operator|+
name|fileName
assert|;
name|this
operator|.
name|entries
operator|=
name|SENTINEL
expr_stmt|;
name|this
operator|.
name|isOpen
operator|=
literal|true
expr_stmt|;
name|writer
operator|=
operator|new
name|CompoundFileWriter
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|handle
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Helper method that reads CFS entries from an input stream */
DECL|method|readEntries
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|readEntries
parameter_list|(
name|IndexInputSlicer
name|handle
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|stream
init|=
name|handle
operator|.
name|openFullSlice
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|mapping
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|firstInt
init|=
name|stream
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// NOTE: as long as we want to throw indexformattooold (vs corruptindexexception), we need
comment|// to read the magic ourselves. See SegmentInfos which also has this.
if|if
condition|(
name|firstInt
operator|==
name|CodecUtil
operator|.
name|CODEC_MAGIC
condition|)
block|{
name|CodecUtil
operator|.
name|checkHeaderNoMagic
argument_list|(
name|stream
argument_list|,
name|CompoundFileWriter
operator|.
name|DATA_CODEC
argument_list|,
name|CompoundFileWriter
operator|.
name|VERSION_START
argument_list|,
name|CompoundFileWriter
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|IndexInput
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|entriesFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|stripExtension
argument_list|(
name|name
argument_list|)
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
decl_stmt|;
name|input
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|entriesFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|CompoundFileWriter
operator|.
name|ENTRY_CODEC
argument_list|,
name|CompoundFileWriter
operator|.
name|VERSION_START
argument_list|,
name|CompoundFileWriter
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numEntries
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|mapping
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CompoundFileDirectory
operator|.
name|FileEntry
argument_list|>
argument_list|(
name|numEntries
argument_list|)
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
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FileEntry
name|fileEntry
init|=
operator|new
name|FileEntry
argument_list|()
decl_stmt|;
specifier|final
name|String
name|id
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|mapping
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
operator|:
literal|"id="
operator|+
name|id
operator|+
literal|" was written multiple times in the CFS"
assert|;
name|mapping
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|fileEntry
argument_list|)
expr_stmt|;
name|fileEntry
operator|.
name|offset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|fileEntry
operator|.
name|length
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|mapping
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|stream
argument_list|,
name|firstInt
argument_list|,
name|CodecUtil
operator|.
name|CODEC_MAGIC
argument_list|,
name|CodecUtil
operator|.
name|CODEC_MAGIC
argument_list|)
throw|;
block|}
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
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isOpen
condition|)
block|{
comment|// allow double close - usually to be consistent with other closeables
return|return;
comment|// already closed
block|}
name|isOpen
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
assert|assert
name|openForWrite
assert|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
specifier|synchronized
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|openForWrite
assert|;
specifier|final
name|String
name|id
init|=
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|FileEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No sub-file with id "
operator|+
name|id
operator|+
literal|" found (fileName="
operator|+
name|name
operator|+
literal|" files: "
operator|+
name|entries
operator|.
name|keySet
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|handle
operator|.
name|openSlice
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|offset
argument_list|,
name|entry
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
index|[]
name|res
decl_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|res
operator|=
name|writer
operator|.
name|listAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|res
operator|=
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
comment|// Add the segment name
name|String
name|seg
init|=
name|IndexFileNames
operator|.
name|parseSegmentName
argument_list|(
name|fileName
argument_list|)
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
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|seg
operator|+
name|res
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/** Returns true iff a file with the given name exists. */
annotation|@
name|Override
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|writer
operator|!=
literal|null
condition|)
block|{
return|return
name|writer
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
name|entries
operator|.
name|containsKey
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException */
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException */
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Returns the length of a file in the directory.    * @throws IOException if the file does not exist */
annotation|@
name|Override
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|writer
operator|!=
literal|null
condition|)
block|{
return|return
name|writer
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
name|FileEntry
name|e
init|=
name|entries
operator|.
name|get
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|e
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException */
annotation|@
name|Override
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|openForWrite
assert|;
specifier|final
name|String
name|id
init|=
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|FileEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No sub-file with id "
operator|+
name|id
operator|+
literal|" found (fileName="
operator|+
name|name
operator|+
literal|" files: "
operator|+
name|entries
operator|.
name|keySet
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
operator|new
name|IndexInputSlicer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|IndexInput
name|openSlice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|handle
operator|.
name|openSlice
argument_list|(
name|sliceDescription
argument_list|,
name|entry
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openFullSlice
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|openSlice
argument_list|(
literal|"full-slice"
argument_list|,
literal|0
argument_list|,
name|entry
operator|.
name|length
argument_list|)
return|;
block|}
block|}
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
literal|"CompoundFileDirectory(file=\""
operator|+
name|fileName
operator|+
literal|"\" in dir="
operator|+
name|directory
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

