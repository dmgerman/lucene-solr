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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|CompoundFormat
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
name|Lock
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
name|StringHelper
import|;
end_import

begin_comment
comment|/**  * plain text compound format.  *<p>  *<b>FOR RECREATIONAL USE ONLY</b>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextCompoundFormat
specifier|public
class|class
name|SimpleTextCompoundFormat
extends|extends
name|CompoundFormat
block|{
comment|/** Sole constructor. */
DECL|method|SimpleTextCompoundFormat
specifier|public
name|SimpleTextCompoundFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getCompoundReader
specifier|public
name|Directory
name|getCompoundReader
parameter_list|(
name|Directory
name|dir
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
name|String
name|dataFile
init|=
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
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|dataFile
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
comment|// first get to TOC:
name|DecimalFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
name|OFFSETPATTERN
argument_list|,
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|pos
init|=
name|in
operator|.
name|length
argument_list|()
operator|-
name|TABLEPOS
operator|.
name|length
operator|-
name|OFFSETPATTERN
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
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
name|TABLEPOS
argument_list|)
assert|;
name|long
name|tablePos
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|tablePos
operator|=
name|df
operator|.
name|parse
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|TABLEPOS
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"can't parse CFS trailer, got: "
operator|+
name|scratch
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|in
argument_list|)
throw|;
block|}
comment|// seek to TOC and read it
name|in
operator|.
name|seek
argument_list|(
name|tablePos
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
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
name|TABLE
argument_list|)
assert|;
name|int
name|numEntries
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|TABLE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fileNames
index|[]
init|=
operator|new
name|String
index|[
name|numEntries
index|]
decl_stmt|;
specifier|final
name|long
name|startOffsets
index|[]
init|=
operator|new
name|long
index|[
name|numEntries
index|]
decl_stmt|;
specifier|final
name|long
name|endOffsets
index|[]
init|=
operator|new
name|long
index|[
name|numEntries
index|]
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
name|numEntries
condition|;
name|i
operator|++
control|)
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
name|TABLENAME
argument_list|)
assert|;
name|fileNames
index|[
name|i
index|]
operator|=
name|si
operator|.
name|name
operator|+
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|TABLENAME
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
comment|// files must be unique and in sorted order
assert|assert
name|fileNames
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|fileNames
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
operator|>
literal|0
assert|;
block|}
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
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
name|TABLESTART
argument_list|)
assert|;
name|startOffsets
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|TABLESTART
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
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
name|TABLEEND
argument_list|)
assert|;
name|endOffsets
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|TABLEEND
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Directory
argument_list|()
block|{
specifier|private
name|int
name|getIndex
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|index
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|fileNames
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No sub-file found (fileName="
operator|+
name|name
operator|+
literal|" files: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|fileNames
argument_list|)
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|index
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fileNames
operator|.
name|clone
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|int
name|index
init|=
name|getIndex
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|endOffsets
index|[
name|index
index|]
operator|-
name|startOffsets
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|int
name|index
init|=
name|getIndex
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|in
operator|.
name|slice
argument_list|(
name|name
argument_list|,
name|startOffsets
index|[
name|index
index|]
argument_list|,
name|endOffsets
index|[
name|index
index|]
operator|-
name|startOffsets
index|[
name|index
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
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
comment|// write methods: disabled
annotation|@
name|Override
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
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
specifier|public
name|Lock
name|obtainLock
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
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
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
name|String
name|dataFile
init|=
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
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
name|int
name|numFiles
init|=
name|si
operator|.
name|files
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
name|names
index|[]
init|=
name|si
operator|.
name|files
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|numFiles
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|long
name|startOffsets
index|[]
init|=
operator|new
name|long
index|[
name|numFiles
index|]
decl_stmt|;
name|long
name|endOffsets
index|[]
init|=
operator|new
name|long
index|[
name|numFiles
index|]
decl_stmt|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
try|try
init|(
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|dataFile
argument_list|,
name|context
argument_list|)
init|)
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// write header for file
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|HEADER
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|names
index|[
name|i
index|]
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// write bytes for file
name|startOffsets
index|[
name|i
index|]
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
try|try
init|(
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|names
index|[
name|i
index|]
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|out
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|in
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|endOffsets
index|[
name|i
index|]
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
name|long
name|tocPos
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// write CFS table
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|TABLE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numFiles
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|TABLENAME
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|names
index|[
name|i
index|]
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|TABLESTART
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|startOffsets
index|[
name|i
index|]
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|TABLEEND
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|endOffsets
index|[
name|i
index|]
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|DecimalFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
name|OFFSETPATTERN
argument_list|,
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|TABLEPOS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|df
operator|.
name|format
argument_list|(
name|tocPos
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|// helper method to strip strip away 'prefix' from 'scratch' and return as String
DECL|method|stripPrefix
specifier|private
name|String
name|stripPrefix
parameter_list|(
name|BytesRefBuilder
name|scratch
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|prefix
operator|.
name|length
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|prefix
operator|.
name|length
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
comment|/** Extension of compound file */
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"scf"
decl_stmt|;
DECL|field|HEADER
specifier|final
specifier|static
name|BytesRef
name|HEADER
init|=
operator|new
name|BytesRef
argument_list|(
literal|"cfs entry for: "
argument_list|)
decl_stmt|;
DECL|field|TABLE
specifier|final
specifier|static
name|BytesRef
name|TABLE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"table of contents, size: "
argument_list|)
decl_stmt|;
DECL|field|TABLENAME
specifier|final
specifier|static
name|BytesRef
name|TABLENAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  filename: "
argument_list|)
decl_stmt|;
DECL|field|TABLESTART
specifier|final
specifier|static
name|BytesRef
name|TABLESTART
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    start: "
argument_list|)
decl_stmt|;
DECL|field|TABLEEND
specifier|final
specifier|static
name|BytesRef
name|TABLEEND
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    end: "
argument_list|)
decl_stmt|;
DECL|field|TABLEPOS
specifier|final
specifier|static
name|BytesRef
name|TABLEPOS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"table of contents begins at offset: "
argument_list|)
decl_stmt|;
DECL|field|OFFSETPATTERN
specifier|final
specifier|static
name|String
name|OFFSETPATTERN
decl_stmt|;
static|static
block|{
name|int
name|numDigits
init|=
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|pattern
index|[]
init|=
operator|new
name|char
index|[
name|numDigits
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|pattern
argument_list|,
literal|'0'
argument_list|)
expr_stmt|;
name|OFFSETPATTERN
operator|=
operator|new
name|String
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

