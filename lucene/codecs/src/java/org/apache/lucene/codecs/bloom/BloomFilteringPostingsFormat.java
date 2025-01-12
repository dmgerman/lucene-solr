begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|bloom
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|PostingsFormat
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
name|bloom
operator|.
name|FuzzySet
operator|.
name|ContainsResult
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
name|PostingsEnum
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

begin_comment
comment|/**  *<p>  * A {@link PostingsFormat} useful for low doc-frequency fields such as primary  * keys. Bloom filters are maintained in a ".blm" file which offers "fast-fail"  * for reads in segments known to have no record of the key. A choice of  * delegate PostingsFormat is used to record all other Postings data.  *</p>  *<p>  * A choice of {@link BloomFilterFactory} can be passed to tailor Bloom Filter  * settings on a per-field basis. The default configuration is  * {@link DefaultBloomFilterFactory} which allocates a ~8mb bitset and hashes  * values using {@link MurmurHash2}. This should be suitable for most purposes.  *</p>  *<p>  * The format of the blm file is as follows:  *</p>  *<ul>  *<li>BloomFilter (.blm) --&gt; Header, DelegatePostingsFormatName,  * NumFilteredFields, Filter<sup>NumFilteredFields</sup>, Footer</li>  *<li>Filter --&gt; FieldNumber, FuzzySet</li>  *<li>FuzzySet --&gt;See {@link FuzzySet#serialize(DataOutput)}</li>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>DelegatePostingsFormatName --&gt; {@link DataOutput#writeString(String)  * String} The name of a ServiceProvider registered {@link PostingsFormat}</li>  *<li>NumFilteredFields --&gt; {@link DataOutput#writeInt Uint32}</li>  *<li>FieldNumber --&gt; {@link DataOutput#writeInt Uint32} The number of the  * field in this segment</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BloomFilteringPostingsFormat
specifier|public
specifier|final
class|class
name|BloomFilteringPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|BLOOM_CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|BLOOM_CODEC_NAME
init|=
literal|"BloomFilter"
decl_stmt|;
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|3
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
comment|/** Extension of Bloom Filters file */
DECL|field|BLOOM_EXTENSION
specifier|static
specifier|final
name|String
name|BLOOM_EXTENSION
init|=
literal|"blm"
decl_stmt|;
DECL|field|bloomFilterFactory
name|BloomFilterFactory
name|bloomFilterFactory
init|=
operator|new
name|DefaultBloomFilterFactory
argument_list|()
decl_stmt|;
DECL|field|delegatePostingsFormat
specifier|private
name|PostingsFormat
name|delegatePostingsFormat
decl_stmt|;
comment|/**    * Creates Bloom filters for a selection of fields created in the index. This    * is recorded as a set of Bitsets held as a segment summary in an additional    * "blm" file. This PostingsFormat delegates to a choice of delegate    * PostingsFormat for encoding all other postings data.    *     * @param delegatePostingsFormat    *          The PostingsFormat that records all the non-bloom filter data i.e.    *          postings info.    * @param bloomFilterFactory    *          The {@link BloomFilterFactory} responsible for sizing BloomFilters    *          appropriately    */
DECL|method|BloomFilteringPostingsFormat
specifier|public
name|BloomFilteringPostingsFormat
parameter_list|(
name|PostingsFormat
name|delegatePostingsFormat
parameter_list|,
name|BloomFilterFactory
name|bloomFilterFactory
parameter_list|)
block|{
name|super
argument_list|(
name|BLOOM_CODEC_NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegatePostingsFormat
operator|=
name|delegatePostingsFormat
expr_stmt|;
name|this
operator|.
name|bloomFilterFactory
operator|=
name|bloomFilterFactory
expr_stmt|;
block|}
comment|/**    * Creates Bloom filters for a selection of fields created in the index. This    * is recorded as a set of Bitsets held as a segment summary in an additional    * "blm" file. This PostingsFormat delegates to a choice of delegate    * PostingsFormat for encoding all other postings data. This choice of    * constructor defaults to the {@link DefaultBloomFilterFactory} for    * configuring per-field BloomFilters.    *     * @param delegatePostingsFormat    *          The PostingsFormat that records all the non-bloom filter data i.e.    *          postings info.    */
DECL|method|BloomFilteringPostingsFormat
specifier|public
name|BloomFilteringPostingsFormat
parameter_list|(
name|PostingsFormat
name|delegatePostingsFormat
parameter_list|)
block|{
name|this
argument_list|(
name|delegatePostingsFormat
argument_list|,
operator|new
name|DefaultBloomFilterFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Used only by core Lucene at read-time via Service Provider instantiation -
comment|// do not use at Write-time in application code.
DECL|method|BloomFilteringPostingsFormat
specifier|public
name|BloomFilteringPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
name|BLOOM_CODEC_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|delegatePostingsFormat
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Error - "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" has been constructed without a choice of PostingsFormat"
argument_list|)
throw|;
block|}
name|FieldsConsumer
name|fieldsConsumer
init|=
name|delegatePostingsFormat
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
return|return
operator|new
name|BloomFilteredFieldsConsumer
argument_list|(
name|fieldsConsumer
argument_list|,
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BloomFilteredFieldsProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|BloomFilteredFieldsProducer
specifier|static
class|class
name|BloomFilteredFieldsProducer
extends|extends
name|FieldsProducer
block|{
DECL|field|delegateFieldsProducer
specifier|private
name|FieldsProducer
name|delegateFieldsProducer
decl_stmt|;
DECL|field|bloomsByFieldName
name|HashMap
argument_list|<
name|String
argument_list|,
name|FuzzySet
argument_list|>
name|bloomsByFieldName
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|BloomFilteredFieldsProducer
specifier|public
name|BloomFilteredFieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|bloomFileName
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
name|BLOOM_EXTENSION
argument_list|)
decl_stmt|;
name|ChecksumIndexInput
name|bloomIn
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|bloomIn
operator|=
name|state
operator|.
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|bloomFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|bloomIn
argument_list|,
name|BLOOM_CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
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
comment|// // Load the hash function used in the BloomFilter
comment|// hashFunction = HashFunction.forName(bloomIn.readString());
comment|// Load the delegate postings format
name|PostingsFormat
name|delegatePostingsFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|bloomIn
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|delegateFieldsProducer
operator|=
name|delegatePostingsFormat
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|int
name|numBlooms
init|=
name|bloomIn
operator|.
name|readInt
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
name|numBlooms
condition|;
name|i
operator|++
control|)
block|{
name|int
name|fieldNum
init|=
name|bloomIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|FuzzySet
name|bloom
init|=
name|FuzzySet
operator|.
name|deserialize
argument_list|(
name|bloomIn
argument_list|)
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|state
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNum
argument_list|)
decl_stmt|;
name|bloomsByFieldName
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|bloom
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|bloomIn
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|bloomIn
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
name|bloomIn
argument_list|,
name|delegateFieldsProducer
argument_list|)
expr_stmt|;
block|}
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
name|delegateFieldsProducer
operator|.
name|iterator
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
name|delegateFieldsProducer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|FuzzySet
name|filter
init|=
name|bloomsByFieldName
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
return|return
name|delegateFieldsProducer
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
else|else
block|{
name|Terms
name|result
init|=
name|delegateFieldsProducer
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|BloomFilteredTerms
argument_list|(
name|result
argument_list|,
name|filter
argument_list|)
return|;
block|}
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
name|delegateFieldsProducer
operator|.
name|size
argument_list|()
return|;
block|}
DECL|class|BloomFilteredTerms
specifier|static
class|class
name|BloomFilteredTerms
extends|extends
name|Terms
block|{
DECL|field|delegateTerms
specifier|private
name|Terms
name|delegateTerms
decl_stmt|;
DECL|field|filter
specifier|private
name|FuzzySet
name|filter
decl_stmt|;
DECL|method|BloomFilteredTerms
specifier|public
name|BloomFilteredTerms
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|FuzzySet
name|filter
parameter_list|)
block|{
name|this
operator|.
name|delegateTerms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|TermsEnum
name|intersect
parameter_list|(
name|CompiledAutomaton
name|compiled
parameter_list|,
specifier|final
name|BytesRef
name|startTerm
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegateTerms
operator|.
name|intersect
argument_list|(
name|compiled
argument_list|,
name|startTerm
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|BloomFilteredTermsEnum
argument_list|(
name|delegateTerms
argument_list|,
name|filter
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
name|delegateTerms
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
name|delegateTerms
operator|.
name|getSumTotalTermFreq
argument_list|()
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
name|delegateTerms
operator|.
name|getSumDocFreq
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
name|delegateTerms
operator|.
name|getDocCount
argument_list|()
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
name|delegateTerms
operator|.
name|hasFreqs
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
name|delegateTerms
operator|.
name|hasOffsets
argument_list|()
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
name|delegateTerms
operator|.
name|hasPositions
argument_list|()
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
name|delegateTerms
operator|.
name|hasPayloads
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMin
specifier|public
name|BytesRef
name|getMin
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTerms
operator|.
name|getMin
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMax
specifier|public
name|BytesRef
name|getMax
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTerms
operator|.
name|getMax
argument_list|()
return|;
block|}
block|}
DECL|class|BloomFilteredTermsEnum
specifier|static
specifier|final
class|class
name|BloomFilteredTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|delegateTerms
specifier|private
name|Terms
name|delegateTerms
decl_stmt|;
DECL|field|delegateTermsEnum
specifier|private
name|TermsEnum
name|delegateTermsEnum
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|FuzzySet
name|filter
decl_stmt|;
DECL|method|BloomFilteredTermsEnum
specifier|public
name|BloomFilteredTermsEnum
parameter_list|(
name|Terms
name|delegateTerms
parameter_list|,
name|FuzzySet
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|delegateTerms
operator|=
name|delegateTerms
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|(
name|Terms
name|delegateTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|delegateTerms
operator|=
name|delegateTerms
expr_stmt|;
name|this
operator|.
name|delegateTermsEnum
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|delegate
specifier|private
name|TermsEnum
name|delegate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|delegateTermsEnum
operator|==
literal|null
condition|)
block|{
comment|/* pull the iterator only if we really need it -            * this can be a relativly heavy operation depending on the             * delegate postings format and they underlying directory            * (clone IndexInput) */
name|delegateTermsEnum
operator|=
name|delegateTerms
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|delegateTermsEnum
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
name|delegate
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
comment|// The magical fail-fast speed up that is the entire point of all of
comment|// this code - save a disk seek if there is a match on an in-memory
comment|// structure
comment|// that may occasionally give a false positive but guaranteed no false
comment|// negatives
if|if
condition|(
name|filter
operator|.
name|contains
argument_list|(
name|text
argument_list|)
operator|==
name|ContainsResult
operator|.
name|NO
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|delegate
argument_list|()
operator|.
name|seekExact
argument_list|(
name|text
argument_list|)
return|;
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
return|return
name|delegate
argument_list|()
operator|.
name|seekCeil
argument_list|(
name|text
argument_list|)
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
block|{
name|delegate
argument_list|()
operator|.
name|seekExact
argument_list|(
name|ord
argument_list|)
expr_stmt|;
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
name|delegate
argument_list|()
operator|.
name|term
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
return|return
name|delegate
argument_list|()
operator|.
name|ord
argument_list|()
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
name|delegate
argument_list|()
operator|.
name|docFreq
argument_list|()
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
name|delegate
argument_list|()
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|postings
specifier|public
name|PostingsEnum
name|postings
parameter_list|(
name|PostingsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
argument_list|()
operator|.
name|postings
argument_list|(
name|reuse
argument_list|,
name|flags
argument_list|)
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
name|long
name|sizeInBytes
init|=
operator|(
operator|(
name|delegateFieldsProducer
operator|!=
literal|null
operator|)
condition|?
name|delegateFieldsProducer
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FuzzySet
argument_list|>
name|entry
range|:
name|bloomsByFieldName
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|length
argument_list|()
operator|*
name|Character
operator|.
name|BYTES
expr_stmt|;
name|sizeInBytes
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
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
name|Collection
argument_list|<
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
name|bloomsByFieldName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|delegateFieldsProducer
operator|!=
literal|null
condition|)
block|{
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
name|delegateFieldsProducer
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|delegateFieldsProducer
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
name|bloomsByFieldName
operator|.
name|size
argument_list|()
operator|+
literal|",delegate="
operator|+
name|delegateFieldsProducer
operator|+
literal|")"
return|;
block|}
block|}
DECL|class|BloomFilteredFieldsConsumer
class|class
name|BloomFilteredFieldsConsumer
extends|extends
name|FieldsConsumer
block|{
DECL|field|delegateFieldsConsumer
specifier|private
name|FieldsConsumer
name|delegateFieldsConsumer
decl_stmt|;
DECL|field|bloomFilters
specifier|private
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|FuzzySet
argument_list|>
name|bloomFilters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|state
specifier|private
name|SegmentWriteState
name|state
decl_stmt|;
DECL|method|BloomFilteredFieldsConsumer
specifier|public
name|BloomFilteredFieldsConsumer
parameter_list|(
name|FieldsConsumer
name|fieldsConsumer
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
block|{
name|this
operator|.
name|delegateFieldsConsumer
operator|=
name|fieldsConsumer
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
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
comment|// Delegate must write first: it may have opened files
comment|// on creating the class
comment|// (e.g. Lucene41PostingsConsumer), and write() will
comment|// close them; alternatively, if we delayed pulling
comment|// the fields consumer until here, we could do it
comment|// afterwards:
name|delegateFieldsConsumer
operator|.
name|write
argument_list|(
name|fields
argument_list|)
expr_stmt|;
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
name|state
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|FuzzySet
name|bloomFilter
init|=
literal|null
decl_stmt|;
name|PostingsEnum
name|postingsEnum
init|=
literal|null
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
if|if
condition|(
name|bloomFilter
operator|==
literal|null
condition|)
block|{
name|bloomFilter
operator|=
name|bloomFilterFactory
operator|.
name|getSetForField
argument_list|(
name|state
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|bloomFilter
operator|==
literal|null
condition|)
block|{
comment|// Field not bloom'd
break|break;
block|}
assert|assert
name|bloomFilters
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
operator|==
literal|false
assert|;
name|bloomFilters
operator|.
name|put
argument_list|(
name|fieldInfo
argument_list|,
name|bloomFilter
argument_list|)
expr_stmt|;
block|}
comment|// Make sure there's at least one doc for this term:
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|PostingsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|bloomFilter
operator|.
name|addValue
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
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
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
name|delegateFieldsConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now we are done accumulating values for these fields
name|List
argument_list|<
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|FuzzySet
argument_list|>
argument_list|>
name|nonSaturatedBlooms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|FuzzySet
argument_list|>
name|entry
range|:
name|bloomFilters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FuzzySet
name|bloomFilter
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|bloomFilterFactory
operator|.
name|isSaturated
argument_list|(
name|bloomFilter
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|nonSaturatedBlooms
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|bloomFileName
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
name|BLOOM_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexOutput
name|bloomOutput
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|bloomFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|bloomOutput
argument_list|,
name|BLOOM_CODEC_NAME
argument_list|,
name|VERSION_CURRENT
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
comment|// remember the name of the postings format we will delegate to
name|bloomOutput
operator|.
name|writeString
argument_list|(
name|delegatePostingsFormat
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// First field in the output file is the number of fields+blooms saved
name|bloomOutput
operator|.
name|writeInt
argument_list|(
name|nonSaturatedBlooms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|FuzzySet
argument_list|>
name|entry
range|:
name|nonSaturatedBlooms
control|)
block|{
name|FieldInfo
name|fieldInfo
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|FuzzySet
name|bloomFilter
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|bloomOutput
operator|.
name|writeInt
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|saveAppropriatelySizedBloomFilter
argument_list|(
name|bloomOutput
argument_list|,
name|bloomFilter
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|bloomOutput
argument_list|)
expr_stmt|;
block|}
comment|//We are done with large bitsets so no need to keep them hanging around
name|bloomFilters
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|saveAppropriatelySizedBloomFilter
specifier|private
name|void
name|saveAppropriatelySizedBloomFilter
parameter_list|(
name|IndexOutput
name|bloomOutput
parameter_list|,
name|FuzzySet
name|bloomFilter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|FuzzySet
name|rightSizedSet
init|=
name|bloomFilterFactory
operator|.
name|downsize
argument_list|(
name|fieldInfo
argument_list|,
name|bloomFilter
argument_list|)
decl_stmt|;
if|if
condition|(
name|rightSizedSet
operator|==
literal|null
condition|)
block|{
name|rightSizedSet
operator|=
name|bloomFilter
expr_stmt|;
block|}
name|rightSizedSet
operator|.
name|serialize
argument_list|(
name|bloomOutput
argument_list|)
expr_stmt|;
block|}
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
literal|"BloomFilteringPostingsFormat("
operator|+
name|delegatePostingsFormat
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

