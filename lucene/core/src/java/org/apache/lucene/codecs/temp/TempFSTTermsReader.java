begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.temp
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Comparator
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
name|SegmentReadState
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
name|TermState
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
name|automaton
operator|.
name|CompiledAutomaton
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
name|automaton
operator|.
name|RunAutomaton
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
name|automaton
operator|.
name|Transition
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
name|fst
operator|.
name|ByteSequenceOutputs
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|Outputs
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
name|fst
operator|.
name|Util
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
name|TempPostingsReaderBase
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

begin_class
DECL|class|TempFSTTermsReader
specifier|public
class|class
name|TempFSTTermsReader
extends|extends
name|FieldsProducer
block|{
DECL|field|postingsReader
specifier|final
name|TempPostingsReaderBase
name|postingsReader
decl_stmt|;
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|fields
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldReader
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldReader
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|TempFSTTermsReader
specifier|public
name|TempFSTTermsReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|TempPostingsReaderBase
name|postingsReader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|termsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|TempFSTTermsWriter
operator|.
name|TERMS_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|postingsReader
operator|=
name|postingsReader
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|termsFileName
argument_list|,
name|state
operator|.
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
name|readHeader
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|postingsReader
operator|.
name|init
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|seekDir
argument_list|(
name|in
argument_list|)
expr_stmt|;
specifier|final
name|FieldInfos
name|fieldInfos
init|=
name|state
operator|.
name|fieldInfos
decl_stmt|;
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|int
name|fieldNumber
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
name|long
name|numTerms
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
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
name|long
name|sumDocFreq
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|int
name|docCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|longsSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|FieldReader
name|current
init|=
operator|new
name|FieldReader
argument_list|(
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|,
name|longsSize
argument_list|)
decl_stmt|;
name|FieldReader
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
name|current
argument_list|)
decl_stmt|;
name|checkFieldSummary
argument_list|(
name|state
operator|.
name|segmentInfo
argument_list|,
name|current
argument_list|,
name|previous
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|readHeader
specifier|private
name|int
name|readHeader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|TempFSTTermsWriter
operator|.
name|TERMS_CODEC_NAME
argument_list|,
name|TempFSTTermsWriter
operator|.
name|TERMS_VERSION_START
argument_list|,
name|TempFSTTermsWriter
operator|.
name|TERMS_VERSION_CURRENT
argument_list|)
return|;
block|}
DECL|method|seekDir
specifier|private
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|length
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFieldSummary
specifier|private
name|void
name|checkFieldSummary
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|FieldReader
name|field
parameter_list|,
name|FieldReader
name|previous
parameter_list|)
throws|throws
name|IOException
block|{
comment|// #docs with field must be<= #docs
if|if
condition|(
name|field
operator|.
name|docCount
argument_list|<
literal|0
operator|||
name|field
operator|.
name|docCount
argument_list|>
name|info
operator|.
name|getDocCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docCount: "
operator|+
name|field
operator|.
name|docCount
operator|+
literal|" maxDoc: "
operator|+
name|info
operator|.
name|getDocCount
argument_list|()
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// #postings must be>= #docs with field
if|if
condition|(
name|field
operator|.
name|sumDocFreq
operator|<
name|field
operator|.
name|docCount
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid sumDocFreq: "
operator|+
name|field
operator|.
name|sumDocFreq
operator|+
literal|" docCount: "
operator|+
name|field
operator|.
name|docCount
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// #positions must be>= #postings
if|if
condition|(
name|field
operator|.
name|sumTotalTermFreq
operator|!=
operator|-
literal|1
operator|&&
name|field
operator|.
name|sumTotalTermFreq
operator|<
name|field
operator|.
name|sumDocFreq
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid sumTotalTermFreq: "
operator|+
name|field
operator|.
name|sumTotalTermFreq
operator|+
literal|" sumDocFreq: "
operator|+
name|field
operator|.
name|sumDocFreq
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
literal|"duplicate fields: "
operator|+
name|field
operator|.
name|fieldInfo
operator|.
name|name
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
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
name|fields
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|FieldReader
specifier|final
class|class
name|FieldReader
extends|extends
name|Terms
block|{
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numTerms
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|final
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|final
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|dict
specifier|final
name|FST
argument_list|<
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|>
name|dict
decl_stmt|;
DECL|method|FieldReader
name|FieldReader
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|numTerms
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|,
name|int
name|longsSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|this
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
name|this
operator|.
name|dict
operator|=
operator|new
name|FST
argument_list|<
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|>
argument_list|(
name|in
argument_list|,
operator|new
name|TempTermOutputs
argument_list|(
name|longsSize
argument_list|)
argument_list|)
expr_stmt|;
comment|//PrintWriter pw = new PrintWriter(new File("../temp/xxx.txt"));
comment|//Util.toDot(dict, pw, false, false);
block|}
comment|// nocommit: implement intersect
comment|// nocommit: why do we need this comparator overridden again and again?
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
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
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
return|return
operator|new
name|SegmentTermsEnum
argument_list|()
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
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
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
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
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
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|numTerms
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
name|sumTotalTermFreq
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
name|sumDocFreq
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
name|docCount
return|;
block|}
comment|// Iterates through terms in this field
DECL|class|SegmentTermsEnum
specifier|private
specifier|final
class|class
name|SegmentTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|method|SegmentTermsEnum
name|SegmentTermsEnum
parameter_list|()
block|{       }
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
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
specifier|final
name|BytesRef
name|target
parameter_list|,
specifier|final
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
literal|null
return|;
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
literal|0
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
literal|0
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
return|return
literal|null
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|BytesRef
name|target
parameter_list|,
name|TermState
name|otherState
parameter_list|)
block|{       }
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
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
block|{       }
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

