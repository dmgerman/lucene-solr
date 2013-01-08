begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Comparator
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
name|PostingsConsumer
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
name|TermStats
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
name|TermsConsumer
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

begin_comment
comment|/**  *<p>  * A {@link PostingsFormat} useful for low doc-frequency fields such as primary  * keys. Bloom filters are maintained in a ".blm" file which offers "fast-fail"  * for reads in segments known to have no record of the key. A choice of  * delegate PostingsFormat is used to record all other Postings data.  *</p>  *<p>  * A choice of {@link BloomFilterFactory} can be passed to tailor Bloom Filter  * settings on a per-field basis. The default configuration is  * {@link DefaultBloomFilterFactory} which allocates a ~8mb bitset and hashes  * values using {@link MurmurHash2}. This should be suitable for most purposes.  *</p>  *<p>  * The format of the blm file is as follows:  *</p>  *<ul>  *<li>BloomFilter (.blm) --&gt; Header, DelegatePostingsFormatName,  * NumFilteredFields, Filter<sup>NumFilteredFields</sup></li>  *<li>Filter --&gt; FieldNumber, FuzzySet</li>  *<li>FuzzySet --&gt;See {@link FuzzySet#serialize(DataOutput)}</li>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>DelegatePostingsFormatName --&gt; {@link DataOutput#writeString(String)  * String} The name of a ServiceProvider registered {@link PostingsFormat}</li>  *<li>NumFilteredFields --&gt; {@link DataOutput#writeInt Uint32}</li>  *<li>FieldNumber --&gt; {@link DataOutput#writeInt Uint32} The number of the  * field in this segment</li>  *</ul>  * @lucene.experimental  */
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
DECL|field|BLOOM_CODEC_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|BLOOM_CODEC_VERSION
init|=
literal|1
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
return|return
operator|new
name|BloomFilteredFieldsConsumer
argument_list|(
name|delegatePostingsFormat
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
argument_list|,
name|state
argument_list|,
name|delegatePostingsFormat
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
specifier|public
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
argument_list|<
name|String
argument_list|,
name|FuzzySet
argument_list|>
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
name|IndexInput
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
name|openInput
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
name|checkHeader
argument_list|(
name|bloomIn
argument_list|,
name|BLOOM_CODEC_NAME
argument_list|,
name|BLOOM_CODEC_VERSION
argument_list|,
name|BLOOM_CODEC_VERSION
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
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|result
decl_stmt|;
if|if
condition|(
operator|(
name|reuse
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|reuse
operator|instanceof
name|BloomFilteredTermsEnum
operator|)
condition|)
block|{
comment|// recycle the existing BloomFilteredTermsEnum by asking the delegate
comment|// to recycle its contained TermsEnum
name|BloomFilteredTermsEnum
name|bfte
init|=
operator|(
name|BloomFilteredTermsEnum
operator|)
name|reuse
decl_stmt|;
if|if
condition|(
name|bfte
operator|.
name|filter
operator|==
name|filter
condition|)
block|{
name|bfte
operator|.
name|delegateTermsEnum
operator|=
name|delegateTerms
operator|.
name|iterator
argument_list|(
name|bfte
operator|.
name|delegateTermsEnum
argument_list|)
expr_stmt|;
return|return
name|bfte
return|;
block|}
block|}
comment|// We have been handed something we cannot reuse (either null, wrong
comment|// class or wrong filter) so allocate a new object
name|result
operator|=
operator|new
name|BloomFilteredTermsEnum
argument_list|(
name|delegateTerms
operator|.
name|iterator
argument_list|(
name|reuse
argument_list|)
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
throws|throws
name|IOException
block|{
return|return
name|delegateTerms
operator|.
name|getComparator
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
block|}
DECL|class|BloomFilteredTermsEnum
class|class
name|BloomFilteredTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|delegateTermsEnum
name|TermsEnum
name|delegateTermsEnum
decl_stmt|;
DECL|field|filter
specifier|private
name|FuzzySet
name|filter
decl_stmt|;
DECL|method|BloomFilteredTermsEnum
specifier|public
name|BloomFilteredTermsEnum
parameter_list|(
name|TermsEnum
name|iterator
parameter_list|,
name|FuzzySet
name|filter
parameter_list|)
block|{
name|this
operator|.
name|delegateTermsEnum
operator|=
name|iterator
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
DECL|method|next
specifier|public
specifier|final
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTermsEnum
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|delegateTermsEnum
operator|.
name|getComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
specifier|final
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
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
name|delegateTermsEnum
operator|.
name|seekExact
argument_list|(
name|text
argument_list|,
name|useCache
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
specifier|final
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegateTermsEnum
operator|.
name|seekCeil
argument_list|(
name|text
argument_list|,
name|useCache
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
specifier|final
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
name|delegateTermsEnum
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
specifier|final
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTermsEnum
operator|.
name|term
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
specifier|final
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTermsEnum
operator|.
name|ord
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTermsEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
specifier|final
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateTermsEnum
operator|.
name|totalTermFreq
argument_list|()
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
name|delegateTermsEnum
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
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
name|delegateTermsEnum
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
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
argument_list|<
name|FieldInfo
argument_list|,
name|FuzzySet
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|state
specifier|private
name|SegmentWriteState
name|state
decl_stmt|;
comment|// private PostingsFormat delegatePostingsFormat;
DECL|method|BloomFilteredFieldsConsumer
specifier|public
name|BloomFilteredFieldsConsumer
parameter_list|(
name|FieldsConsumer
name|fieldsConsumer
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|,
name|PostingsFormat
name|delegatePostingsFormat
parameter_list|)
block|{
name|this
operator|.
name|delegateFieldsConsumer
operator|=
name|fieldsConsumer
expr_stmt|;
comment|// this.delegatePostingsFormat=delegatePostingsFormat;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|FuzzySet
name|bloomFilter
init|=
name|bloomFilterFactory
operator|.
name|getSetForField
argument_list|(
name|state
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bloomFilter
operator|!=
literal|null
condition|)
block|{
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
name|field
argument_list|,
name|bloomFilter
argument_list|)
expr_stmt|;
return|return
operator|new
name|WrappedTermsConsumer
argument_list|(
name|delegateFieldsConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
argument_list|,
name|bloomFilter
argument_list|)
return|;
block|}
else|else
block|{
comment|// No, use the unfiltered fieldsConsumer - we are not interested in
comment|// recording any term Bitsets.
return|return
name|delegateFieldsConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
return|;
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
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|FuzzySet
argument_list|>
argument_list|>
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
name|IndexOutput
name|bloomOutput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bloomOutput
operator|=
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
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|bloomOutput
argument_list|,
name|BLOOM_CODEC_NAME
argument_list|,
name|BLOOM_CODEC_VERSION
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
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
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
DECL|class|WrappedTermsConsumer
class|class
name|WrappedTermsConsumer
extends|extends
name|TermsConsumer
block|{
DECL|field|delegateTermsConsumer
specifier|private
name|TermsConsumer
name|delegateTermsConsumer
decl_stmt|;
DECL|field|bloomFilter
specifier|private
name|FuzzySet
name|bloomFilter
decl_stmt|;
DECL|method|WrappedTermsConsumer
specifier|public
name|WrappedTermsConsumer
parameter_list|(
name|TermsConsumer
name|termsConsumer
parameter_list|,
name|FuzzySet
name|bloomFilter
parameter_list|)
block|{
name|this
operator|.
name|delegateTermsConsumer
operator|=
name|termsConsumer
expr_stmt|;
name|this
operator|.
name|bloomFilter
operator|=
name|bloomFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegateTermsConsumer
operator|.
name|startTerm
argument_list|(
name|text
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Record this term in our BloomFilter
if|if
condition|(
name|stats
operator|.
name|docFreq
operator|>
literal|0
condition|)
block|{
name|bloomFilter
operator|.
name|addValue
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
name|delegateTermsConsumer
operator|.
name|finishTerm
argument_list|(
name|text
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|delegateTermsConsumer
operator|.
name|finish
argument_list|(
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
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
throws|throws
name|IOException
block|{
return|return
name|delegateTermsConsumer
operator|.
name|getComparator
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

