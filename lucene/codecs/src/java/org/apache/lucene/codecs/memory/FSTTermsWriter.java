begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|FieldsConsumer
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
name|PostingsWriterBase
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
name|SegmentWriteState
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
name|DataOutput
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
name|RAMOutputStream
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
name|FixedBitSet
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
name|IntsRefBuilder
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
name|Builder
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
name|Util
import|;
end_import

begin_comment
comment|/**  * FST-based term dict, using metadata as FST output.  *  * The FST directly holds the mapping between&lt;term, metadata&gt;.  *  * Term metadata consists of three parts:  * 1. term statistics: docFreq, totalTermFreq;  * 2. monotonic long[], e.g. the pointer to the postings list for that term;  * 3. generic byte[], e.g. other information need by postings reader.  *  *<p>  * File:  *<ul>  *<li><tt>.tst</tt>:<a href="#Termdictionary">Term Dictionary</a></li>  *</ul>  *<p>  *  *<a name="Termdictionary"></a>  *<h3>Term Dictionary</h3>  *<p>  *  The .tst contains a list of FSTs, one for each field.  *  The FST maps a term to its corresponding statistics (e.g. docfreq)   *  and metadata (e.g. information for postings list reader like file pointer  *  to postings list).  *</p>  *<p>  *  Typically the metadata is separated into two parts:  *<ul>  *<li>  *    Monotonical long array: Some metadata will always be ascending in order  *    with the corresponding term. This part is used by FST to share outputs between arcs.  *</li>  *<li>  *    Generic byte array: Used to store non-monotonic metadata.  *</li>  *</ul>  *  * File format:  *<ul>  *<li>TermsDict(.tst) --&gt; Header,<i>PostingsHeader</i>, FieldSummary, DirOffset</li>  *<li>FieldSummary --&gt; NumFields,&lt;FieldNumber, NumTerms, SumTotalTermFreq?,   *                                      SumDocFreq, DocCount, LongsSize, TermFST&gt;<sup>NumFields</sup></li>  *<li>TermFST --&gt; {@link FST FST&lt;TermData&gt;}</li>  *<li>TermData --&gt; Flag, BytesSize?, LongDelta<sup>LongsSize</sup>?, Byte<sup>BytesSize</sup>?,   *&lt; DocFreq[Same?], (TotalTermFreq-DocFreq)&gt; ?</li>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>DirOffset --&gt; {@link DataOutput#writeLong Uint64}</li>  *<li>DocFreq, LongsSize, BytesSize, NumFields,  *        FieldNumber, DocCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>TotalTermFreq, NumTerms, SumTotalTermFreq, SumDocFreq, LongDelta --&gt;   *        {@link DataOutput#writeVLong VLong}</li>  *</ul>  *<p>Notes:</p>  *<ul>  *<li>  *   The format of PostingsHeader and generic meta bytes are customized by the specific postings implementation:  *   they contain arbitrary per-file data (such as parameters or versioning information), and per-term data  *   (non-monotonic ones like pulsed postings data).  *</li>  *<li>  *   The format of TermData is determined by FST, typically monotonic metadata will be dense around shallow arcs,  *   while in deeper arcs only generic bytes and term statistics exist.  *</li>  *<li>  *   The byte Flag is used to indicate which part of metadata exists on current arc. Specially the monotonic part  *   is omitted when it is an array of 0s.  *</li>  *<li>  *   Since LongsSize is per-field fixed, it is only written once in field summary.  *</li>  *</ul>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FSTTermsWriter
specifier|public
class|class
name|FSTTermsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|TERMS_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_EXTENSION
init|=
literal|"tfp"
decl_stmt|;
DECL|field|TERMS_CODEC_NAME
specifier|static
specifier|final
name|String
name|TERMS_CODEC_NAME
init|=
literal|"FSTTerms"
decl_stmt|;
DECL|field|TERMS_VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|TERMS_VERSION_START
init|=
literal|2
decl_stmt|;
DECL|field|TERMS_VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|TERMS_VERSION_CURRENT
init|=
name|TERMS_VERSION_START
decl_stmt|;
DECL|field|postingsWriter
specifier|final
name|PostingsWriterBase
name|postingsWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|out
name|IndexOutput
name|out
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|fields
specifier|final
name|List
argument_list|<
name|FieldMetaData
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|FSTTermsWriter
specifier|public
name|FSTTermsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|PostingsWriterBase
name|postingsWriter
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
name|TERMS_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|out
argument_list|,
name|TERMS_CODEC_NAME
argument_list|,
name|TERMS_VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|.
name|init
argument_list|(
name|out
argument_list|,
name|state
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
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeTrailer
specifier|private
name|void
name|writeTrailer
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
name|dirStart
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
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
name|boolean
name|hasFreq
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TermsWriter
name|termsWriter
init|=
operator|new
name|TermsWriter
argument_list|(
name|fieldInfo
argument_list|)
decl_stmt|;
name|long
name|sumTotalTermFreq
init|=
literal|0
decl_stmt|;
name|long
name|sumDocFreq
init|=
literal|0
decl_stmt|;
name|FixedBitSet
name|docsSeen
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|BlockTermState
name|termState
init|=
name|postingsWriter
operator|.
name|writeTerm
argument_list|(
name|term
argument_list|,
name|termsEnum
argument_list|,
name|docsSeen
argument_list|)
decl_stmt|;
if|if
condition|(
name|termState
operator|!=
literal|null
condition|)
block|{
name|termsWriter
operator|.
name|finishTerm
argument_list|(
name|term
argument_list|,
name|termState
argument_list|)
expr_stmt|;
name|sumTotalTermFreq
operator|+=
name|termState
operator|.
name|totalTermFreq
expr_stmt|;
name|sumDocFreq
operator|+=
name|termState
operator|.
name|docFreq
expr_stmt|;
block|}
block|}
name|termsWriter
operator|.
name|finish
argument_list|(
name|hasFreq
condition|?
name|sumTotalTermFreq
else|:
operator|-
literal|1
argument_list|,
name|sumDocFreq
argument_list|,
name|docsSeen
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|out
operator|!=
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
comment|// write field summary
specifier|final
name|long
name|dirStart
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldMetaData
name|field
range|:
name|fields
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|numTerms
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumDocFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|longsSize
argument_list|)
expr_stmt|;
name|field
operator|.
name|dict
operator|.
name|save
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|writeTrailer
argument_list|(
name|out
argument_list|,
name|dirStart
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|out
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|class|FieldMetaData
specifier|private
specifier|static
class|class
name|FieldMetaData
block|{
DECL|field|fieldInfo
specifier|public
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numTerms
specifier|public
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|public
specifier|final
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|public
specifier|final
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|public
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
specifier|public
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|dict
specifier|public
specifier|final
name|FST
argument_list|<
name|FSTTermOutputs
operator|.
name|TermData
argument_list|>
name|dict
decl_stmt|;
DECL|method|FieldMetaData
specifier|public
name|FieldMetaData
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
parameter_list|,
name|FST
argument_list|<
name|FSTTermOutputs
operator|.
name|TermData
argument_list|>
name|fst
parameter_list|)
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
name|fst
expr_stmt|;
block|}
block|}
DECL|class|TermsWriter
specifier|final
class|class
name|TermsWriter
block|{
DECL|field|builder
specifier|private
specifier|final
name|Builder
argument_list|<
name|FSTTermOutputs
operator|.
name|TermData
argument_list|>
name|builder
decl_stmt|;
DECL|field|outputs
specifier|private
specifier|final
name|FSTTermOutputs
name|outputs
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|longsSize
specifier|private
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|numTerms
specifier|private
name|long
name|numTerms
decl_stmt|;
DECL|field|scratchTerm
specifier|private
specifier|final
name|IntsRefBuilder
name|scratchTerm
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|metaWriter
specifier|private
specifier|final
name|RAMOutputStream
name|metaWriter
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|method|TermsWriter
name|TermsWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|numTerms
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|postingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|outputs
operator|=
operator|new
name|FSTTermOutputs
argument_list|(
name|fieldInfo
argument_list|,
name|longsSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
block|}
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|BlockTermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write term meta data into fst
specifier|final
name|FSTTermOutputs
operator|.
name|TermData
name|meta
init|=
operator|new
name|FSTTermOutputs
operator|.
name|TermData
argument_list|()
decl_stmt|;
name|meta
operator|.
name|longs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|meta
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
name|meta
operator|.
name|docFreq
operator|=
name|state
operator|.
name|docFreq
expr_stmt|;
name|meta
operator|.
name|totalTermFreq
operator|=
name|state
operator|.
name|totalTermFreq
expr_stmt|;
name|postingsWriter
operator|.
name|encodeTerm
argument_list|(
name|meta
operator|.
name|longs
argument_list|,
name|metaWriter
argument_list|,
name|fieldInfo
argument_list|,
name|state
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bytesSize
init|=
operator|(
name|int
operator|)
name|metaWriter
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytesSize
operator|>
literal|0
condition|)
block|{
name|meta
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytesSize
index|]
expr_stmt|;
name|metaWriter
operator|.
name|writeTo
argument_list|(
name|meta
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metaWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|text
argument_list|,
name|scratchTerm
argument_list|)
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|numTerms
operator|++
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
comment|// save FST dict
if|if
condition|(
name|numTerms
operator|>
literal|0
condition|)
block|{
specifier|final
name|FST
argument_list|<
name|FSTTermOutputs
operator|.
name|TermData
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FieldMetaData
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
argument_list|,
name|fst
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

