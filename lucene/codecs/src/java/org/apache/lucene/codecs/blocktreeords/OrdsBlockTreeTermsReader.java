begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.blocktreeords
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktreeords
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
name|ArrayList
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
name|List
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
name|FieldsProducer
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
name|PostingsReaderBase
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
name|blocktreeords
operator|.
name|FSTOrdsOutputs
operator|.
name|Output
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
name|Accountable
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
name|Accountables
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

begin_comment
comment|/**  * See {@link OrdsBlockTreeTermsWriter}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|OrdsBlockTreeTermsReader
specifier|public
specifier|final
class|class
name|OrdsBlockTreeTermsReader
extends|extends
name|FieldsProducer
block|{
comment|// Open input to the main terms dict file (_X.tiv)
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
comment|//private static final boolean DEBUG = BlockTreeTermsWriter.DEBUG;
comment|// Reads the terms dict entries, to gather state to
comment|// produce DocsEnum on demand
DECL|field|postingsReader
specifier|final
name|PostingsReaderBase
name|postingsReader
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|OrdsFieldReader
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** File offset where the directory starts in the terms file. */
DECL|field|dirOffset
specifier|private
name|long
name|dirOffset
decl_stmt|;
comment|/** File offset where the directory starts in the index file. */
DECL|field|indexDirOffset
specifier|private
name|long
name|indexDirOffset
decl_stmt|;
DECL|field|segment
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|OrdsBlockTreeTermsReader
specifier|public
name|OrdsBlockTreeTermsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|PostingsReaderBase
name|postingsReader
parameter_list|,
name|IOContext
name|ioContext
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|postingsReader
operator|=
name|postingsReader
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|info
operator|.
name|name
expr_stmt|;
name|in
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexInput
name|indexIn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|TERMS_CODEC_NAME
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|VERSION_START
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|indexIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
name|int
name|indexVersion
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|indexIn
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|TERMS_INDEX_CODEC_NAME
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|VERSION_START
argument_list|,
name|OrdsBlockTreeTermsWriter
operator|.
name|VERSION_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexVersion
operator|!=
name|version
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"mixmatched version files: "
operator|+
name|in
operator|+
literal|"="
operator|+
name|version
operator|+
literal|","
operator|+
name|indexIn
operator|+
literal|"="
operator|+
name|indexVersion
argument_list|,
name|indexIn
argument_list|)
throw|;
block|}
comment|// verify
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|indexIn
argument_list|)
expr_stmt|;
comment|// Have PostingsReader init itself
name|postingsReader
operator|.
name|init
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// NOTE: data file is too costly to verify checksum against all the bytes on open,
comment|// but for now we at least verify proper structure of the checksum footer: which looks
comment|// for FOOTER_MAGIC + algorithmID. This is cheap and can detect some forms of corruption
comment|// such as file truncation.
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Read per-field details
name|seekDir
argument_list|(
name|in
argument_list|,
name|dirOffset
argument_list|)
expr_stmt|;
name|seekDir
argument_list|(
name|indexIn
argument_list|,
name|indexDirOffset
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numFields
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid numFields: "
operator|+
name|numFields
argument_list|,
name|in
argument_list|)
throw|;
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
specifier|final
name|int
name|field
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|numTerms
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
assert|assert
name|numTerms
operator|>=
literal|0
assert|;
comment|// System.out.println("read field=" + field + " numTerms=" + numTerms + " i=" + i);
specifier|final
name|int
name|numBytes
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|code
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
name|numBytes
index|]
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|code
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|code
operator|.
name|length
operator|=
name|numBytes
expr_stmt|;
specifier|final
name|Output
name|rootCode
init|=
name|OrdsBlockTreeTermsWriter
operator|.
name|FST_OUTPUTS
operator|.
name|newOutput
argument_list|(
name|code
argument_list|,
literal|0
argument_list|,
name|numTerms
argument_list|)
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|fieldInfo
operator|!=
literal|null
operator|:
literal|"field="
operator|+
name|field
assert|;
assert|assert
name|numTerms
operator|<=
name|Integer
operator|.
name|MAX_VALUE
assert|;
specifier|final
name|long
name|sumTotalTermFreq
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|?
operator|-
literal|1
else|:
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|sumDocFreq
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|longsSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|// System.out.println("  longsSize=" + longsSize);
name|BytesRef
name|minTerm
init|=
name|readBytesRef
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|BytesRef
name|maxTerm
init|=
name|readBytesRef
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|docCount
argument_list|<
literal|0
operator|||
name|docCount
argument_list|>
name|info
operator|.
name|getDocCount
argument_list|()
condition|)
block|{
comment|// #docs with field must be<= #docs
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docCount: "
operator|+
name|docCount
operator|+
literal|" maxDoc: "
operator|+
name|info
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|in
argument_list|)
throw|;
block|}
if|if
condition|(
name|sumDocFreq
operator|<
name|docCount
condition|)
block|{
comment|// #postings must be>= #docs with field
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid sumDocFreq: "
operator|+
name|sumDocFreq
operator|+
literal|" docCount: "
operator|+
name|docCount
argument_list|,
name|in
argument_list|)
throw|;
block|}
if|if
condition|(
name|sumTotalTermFreq
operator|!=
operator|-
literal|1
operator|&&
name|sumTotalTermFreq
operator|<
name|sumDocFreq
condition|)
block|{
comment|// #positions must be>= #postings
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid sumTotalTermFreq: "
operator|+
name|sumTotalTermFreq
operator|+
literal|" sumDocFreq: "
operator|+
name|sumDocFreq
argument_list|,
name|in
argument_list|)
throw|;
block|}
specifier|final
name|long
name|indexStartFP
init|=
name|indexIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|OrdsFieldReader
name|previous
init|=
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|OrdsFieldReader
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|rootCode
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|,
name|indexStartFP
argument_list|,
name|longsSize
argument_list|,
name|indexIn
argument_list|,
name|minTerm
argument_list|,
name|maxTerm
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"duplicate field: "
operator|+
name|fieldInfo
operator|.
name|name
argument_list|,
name|in
argument_list|)
throw|;
block|}
block|}
name|indexIn
operator|.
name|close
argument_list|()
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
comment|// this.close() will close in:
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|indexIn
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readBytesRef
specifier|private
specifier|static
name|BytesRef
name|readBytesRef
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|bytes
operator|.
name|length
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|bytes
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/** Seek {@code input} to the directory offset. */
DECL|method|seekDir
specifier|private
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
name|CodecUtil
operator|.
name|footerLength
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
name|dirOffset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
block|}
comment|// for debugging
comment|// private static String toHex(int v) {
comment|//   return "0x" + Integer.toHexString(v);
comment|// }
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
argument_list|,
name|postingsReader
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Clear so refs to terms index is GCable even if
comment|// app hangs onto us:
name|fields
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
assert|assert
name|field
operator|!=
literal|null
assert|;
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
comment|// for debugging
DECL|method|brToString
name|String
name|brToString
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|b
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" "
operator|+
name|b
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// If BytesRef isn't actually UTF8, or it's eg a
comment|// prefix of UTF8 that ends mid-unicode-char, we
comment|// fallback to hex:
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|long
name|sizeInBytes
init|=
name|postingsReader
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
for|for
control|(
name|OrdsFieldReader
name|reader
range|:
name|fields
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|reader
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|resources
operator|.
name|addAll
argument_list|(
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"field"
argument_list|,
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"delegate"
argument_list|,
name|postingsReader
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
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
block|{
comment|// term dictionary
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// postings
name|postingsReader
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
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
operator|+
literal|"(fields="
operator|+
name|fields
operator|.
name|size
argument_list|()
operator|+
literal|",delegate="
operator|+
name|postingsReader
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

