begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|HashMap
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
name|FieldInfosWriter
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
name|PerDocConsumer
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
name|StoredFieldsWriter
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
name|TermVectorsWriter
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
name|InfoStream
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
name|ReaderUtil
import|;
end_import

begin_comment
comment|/**  * The SegmentMerger class combines two or more Segments, represented by an IndexReader ({@link #add},  * into a single Segment.  After adding the appropriate readers, call the merge method to combine the  * segments.  *  * @see #merge  * @see #add  */
end_comment

begin_class
DECL|class|SegmentMerger
specifier|final
class|class
name|SegmentMerger
block|{
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|termIndexInterval
specifier|private
specifier|final
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|field|mergeState
specifier|private
specifier|final
name|MergeState
name|mergeState
init|=
operator|new
name|MergeState
argument_list|()
decl_stmt|;
DECL|method|SegmentMerger
name|SegmentMerger
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|termIndexInterval
parameter_list|,
name|String
name|name
parameter_list|,
name|MergeState
operator|.
name|CheckAbort
name|checkAbort
parameter_list|,
name|PayloadProcessorProvider
name|payloadProcessorProvider
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|Codec
name|codec
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|mergeState
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|mergeState
operator|.
name|readers
operator|=
operator|new
name|ArrayList
argument_list|<
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
argument_list|>
argument_list|()
expr_stmt|;
name|mergeState
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|=
name|checkAbort
expr_stmt|;
name|mergeState
operator|.
name|payloadProcessorProvider
operator|=
name|payloadProcessorProvider
expr_stmt|;
name|directory
operator|=
name|dir
expr_stmt|;
name|segment
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|termIndexInterval
operator|=
name|termIndexInterval
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Add an IndexReader to the collection of readers that are to be merged    * @param reader    */
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
try|try
block|{
operator|new
name|ReaderUtil
operator|.
name|Gather
argument_list|(
name|reader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicIndexReader
name|r
parameter_list|)
block|{
name|mergeState
operator|.
name|readers
operator|.
name|add
argument_list|(
operator|new
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
argument_list|(
name|r
argument_list|,
name|r
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// won't happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|SegmentReader
name|reader
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
block|{
name|mergeState
operator|.
name|readers
operator|.
name|add
argument_list|(
operator|new
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
argument_list|(
name|reader
argument_list|,
name|liveDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the readers specified by the {@link #add} method into the directory passed to the constructor    * @return The number of documents that were merged    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|merge
specifier|final
name|MergeState
name|merge
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
comment|// NOTE: it's important to add calls to
comment|// checkAbort.work(...) if you make any changes to this
comment|// method that will spend alot of time.  The frequency
comment|// of this check impacts how long
comment|// IndexWriter.close(false) takes to actually stop the
comment|// threads.
name|mergeState
operator|.
name|mergedDocCount
operator|=
name|setDocMaps
argument_list|()
expr_stmt|;
name|mergeFieldInfos
argument_list|()
expr_stmt|;
name|setMatchingSegmentReaders
argument_list|()
expr_stmt|;
name|int
name|numMerged
init|=
name|mergeFields
argument_list|()
decl_stmt|;
assert|assert
name|numMerged
operator|==
name|mergeState
operator|.
name|mergedDocCount
assert|;
specifier|final
name|SegmentWriteState
name|segmentWriteState
init|=
operator|new
name|SegmentWriteState
argument_list|(
name|mergeState
operator|.
name|infoStream
argument_list|,
name|directory
argument_list|,
name|segment
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|,
name|mergeState
operator|.
name|mergedDocCount
argument_list|,
name|termIndexInterval
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|mergeTerms
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
name|mergePerDoc
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|fieldInfos
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|mergeNorms
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
name|numMerged
operator|=
name|mergeVectors
argument_list|()
expr_stmt|;
assert|assert
name|numMerged
operator|==
name|mergeState
operator|.
name|mergedDocCount
assert|;
block|}
return|return
name|mergeState
return|;
block|}
DECL|method|setMatchingSegmentReaders
specifier|private
name|void
name|setMatchingSegmentReaders
parameter_list|()
block|{
comment|// If the i'th reader is a SegmentReader and has
comment|// identical fieldName -> number mapping, then this
comment|// array will be non-null at position i:
name|int
name|numReaders
init|=
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|matchingSegmentReaders
operator|=
operator|new
name|SegmentReader
index|[
name|numReaders
index|]
expr_stmt|;
comment|// If this reader is a SegmentReader, and all of its
comment|// field name -> number mappings match the "merged"
comment|// FieldInfos, then we can do a bulk copy of the
comment|// stored fields:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// TODO: we may be able to broaden this to
comment|// non-SegmentReaders, since FieldInfos is now
comment|// required?  But... this'd also require exposing
comment|// bulk-copy (TVs and stored fields) API in foreign
comment|// readers..
if|if
condition|(
name|reader
operator|.
name|reader
operator|instanceof
name|SegmentReader
condition|)
block|{
name|SegmentReader
name|segmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|reader
operator|.
name|reader
decl_stmt|;
name|boolean
name|same
init|=
literal|true
decl_stmt|;
name|FieldInfos
name|segmentFieldInfos
init|=
name|segmentReader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|segmentFieldInfos
control|)
block|{
if|if
condition|(
operator|!
name|mergeState
operator|.
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|fi
operator|.
name|number
argument_list|)
operator|.
name|equals
argument_list|(
name|fi
operator|.
name|name
argument_list|)
condition|)
block|{
name|same
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|same
condition|)
block|{
name|mergeState
operator|.
name|matchingSegmentReaders
index|[
name|i
index|]
operator|=
name|segmentReader
expr_stmt|;
name|mergeState
operator|.
name|matchedCount
operator|++
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
literal|"merge store matchedCount="
operator|+
name|mergeState
operator|.
name|matchedCount
operator|+
literal|" vs "
operator|+
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|matchedCount
operator|!=
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|)
block|{
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
literal|""
operator|+
operator|(
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
operator|-
name|mergeState
operator|.
name|matchedCount
operator|)
operator|+
literal|" non-bulk merges"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// returns an updated typepromoter (tracking type and size) given a previous one,
comment|// and a newly encountered docvalues
DECL|method|mergeDocValuesType
specifier|private
name|TypePromoter
name|mergeDocValuesType
parameter_list|(
name|TypePromoter
name|previous
parameter_list|,
name|DocValues
name|docValues
parameter_list|)
block|{
name|TypePromoter
name|incoming
init|=
name|TypePromoter
operator|.
name|create
argument_list|(
name|docValues
operator|.
name|type
argument_list|()
argument_list|,
name|docValues
operator|.
name|getValueSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|previous
operator|=
name|TypePromoter
operator|.
name|getIdentityPromoter
argument_list|()
expr_stmt|;
block|}
name|TypePromoter
name|promoted
init|=
name|previous
operator|.
name|promote
argument_list|(
name|incoming
argument_list|)
decl_stmt|;
if|if
condition|(
name|promoted
operator|==
literal|null
condition|)
block|{
comment|// type is incompatible: promote to BYTES_VAR_STRAIGHT
return|return
name|TypePromoter
operator|.
name|create
argument_list|(
name|DocValues
operator|.
name|Type
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|,
name|TypePromoter
operator|.
name|VAR_TYPE_VALUE_SIZE
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|promoted
return|;
block|}
block|}
DECL|method|mergeFieldInfos
specifier|private
name|void
name|mergeFieldInfos
parameter_list|()
throws|throws
name|IOException
block|{
name|mergeDocValuesAndNormsFieldInfos
argument_list|()
expr_stmt|;
comment|// write the merged infos
name|FieldInfosWriter
name|fieldInfosWriter
init|=
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosWriter
argument_list|()
decl_stmt|;
name|fieldInfosWriter
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|mergeDocValuesAndNormsFieldInfos
specifier|public
name|void
name|mergeDocValuesAndNormsFieldInfos
parameter_list|()
throws|throws
name|IOException
block|{
comment|// mapping from all docvalues fields found to their promoted types
comment|// this is because FieldInfos does not store the valueSize
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|TypePromoter
argument_list|>
name|docValuesTypes
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|TypePromoter
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|TypePromoter
argument_list|>
name|normValuesTypes
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|TypePromoter
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|readerAndLiveDocs
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|AtomicIndexReader
name|reader
init|=
name|readerAndLiveDocs
operator|.
name|reader
decl_stmt|;
name|FieldInfos
name|readerFieldInfos
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|readerFieldInfos
control|)
block|{
name|FieldInfo
name|merged
init|=
name|mergeState
operator|.
name|fieldInfos
operator|.
name|add
argument_list|(
name|fi
argument_list|)
decl_stmt|;
comment|// update the type promotion mapping for this reader
if|if
condition|(
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|TypePromoter
name|previous
init|=
name|docValuesTypes
operator|.
name|get
argument_list|(
name|merged
argument_list|)
decl_stmt|;
name|docValuesTypes
operator|.
name|put
argument_list|(
name|merged
argument_list|,
name|mergeDocValuesType
argument_list|(
name|previous
argument_list|,
name|reader
operator|.
name|docValues
argument_list|(
name|fi
operator|.
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fi
operator|.
name|normsPresent
argument_list|()
condition|)
block|{
name|TypePromoter
name|previous
init|=
name|normValuesTypes
operator|.
name|get
argument_list|(
name|merged
argument_list|)
decl_stmt|;
name|normValuesTypes
operator|.
name|put
argument_list|(
name|merged
argument_list|,
name|mergeDocValuesType
argument_list|(
name|previous
argument_list|,
name|reader
operator|.
name|normValues
argument_list|(
name|fi
operator|.
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|updatePromoted
argument_list|(
name|normValuesTypes
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|updatePromoted
argument_list|(
name|docValuesTypes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|updatePromoted
specifier|protected
name|void
name|updatePromoted
parameter_list|(
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|TypePromoter
argument_list|>
name|infoAndPromoter
parameter_list|,
name|boolean
name|norms
parameter_list|)
block|{
comment|// update any promoted doc values types:
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|TypePromoter
argument_list|>
name|e
range|:
name|infoAndPromoter
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FieldInfo
name|fi
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TypePromoter
name|promoter
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|promoter
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|norms
condition|)
block|{
name|fi
operator|.
name|setNormValueType
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fi
operator|.
name|setDocValuesType
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|promoter
operator|!=
name|TypePromoter
operator|.
name|getIdentityPromoter
argument_list|()
assert|;
if|if
condition|(
name|norms
condition|)
block|{
if|if
condition|(
name|fi
operator|.
name|getNormType
argument_list|()
operator|!=
name|promoter
operator|.
name|type
argument_list|()
condition|)
block|{
comment|// reset the type if we got promoted
name|fi
operator|.
name|setNormValueType
argument_list|(
name|promoter
operator|.
name|type
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|promoter
operator|.
name|type
argument_list|()
condition|)
block|{
comment|// reset the type if we got promoted
name|fi
operator|.
name|setDocValuesType
argument_list|(
name|promoter
operator|.
name|type
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    *    * @return The number of documents in all of the readers    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|mergeFields
specifier|private
name|int
name|mergeFields
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|StoredFieldsWriter
name|fieldsWriter
init|=
name|codec
operator|.
name|storedFieldsFormat
argument_list|()
operator|.
name|fieldsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|fieldsWriter
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
return|;
block|}
finally|finally
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Merge the TermVectors from each of the segments into the new one.    * @throws IOException    */
DECL|method|mergeVectors
specifier|private
specifier|final
name|int
name|mergeVectors
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|TermVectorsWriter
name|termVectorsWriter
init|=
name|codec
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|termVectorsWriter
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
return|;
block|}
finally|finally
block|{
name|termVectorsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// NOTE: removes any "all deleted" readers from mergeState.readers
DECL|method|setDocMaps
specifier|private
name|int
name|setDocMaps
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numReaders
init|=
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Remap docIDs
name|mergeState
operator|.
name|docMaps
operator|=
operator|new
name|int
index|[
name|numReaders
index|]
index|[]
expr_stmt|;
name|mergeState
operator|.
name|docBase
operator|=
operator|new
name|int
index|[
name|numReaders
index|]
expr_stmt|;
name|mergeState
operator|.
name|dirPayloadProcessor
operator|=
operator|new
name|PayloadProcessorProvider
operator|.
name|DirPayloadProcessor
index|[
name|numReaders
index|]
expr_stmt|;
name|mergeState
operator|.
name|currentPayloadProcessor
operator|=
operator|new
name|PayloadProcessorProvider
operator|.
name|PayloadProcessor
index|[
name|numReaders
index|]
expr_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|mergeState
operator|.
name|docBase
index|[
name|i
index|]
operator|=
name|docBase
expr_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docCount
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|liveDocs
decl_stmt|;
specifier|final
name|int
index|[]
name|docMap
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
name|int
name|delCount
init|=
literal|0
decl_stmt|;
name|docMap
operator|=
operator|new
name|int
index|[
name|maxDoc
index|]
expr_stmt|;
name|int
name|newDocID
init|=
literal|0
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
name|maxDoc
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|docMap
index|[
name|j
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|delCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|docMap
index|[
name|j
index|]
operator|=
name|newDocID
operator|++
expr_stmt|;
block|}
block|}
name|docCount
operator|=
name|maxDoc
operator|-
name|delCount
expr_stmt|;
block|}
else|else
block|{
name|docCount
operator|=
name|maxDoc
expr_stmt|;
name|docMap
operator|=
literal|null
expr_stmt|;
block|}
name|mergeState
operator|.
name|docMaps
index|[
name|i
index|]
operator|=
name|docMap
expr_stmt|;
name|docBase
operator|+=
name|docCount
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|payloadProcessorProvider
operator|!=
literal|null
condition|)
block|{
comment|// nocommit: this was original, is the change correct:
comment|// mergeState.dirPayloadProcessor[i] = mergeState.payloadProcessorProvider.getDirProcessor(reader.reader.directory());
name|mergeState
operator|.
name|dirPayloadProcessor
index|[
name|i
index|]
operator|=
name|mergeState
operator|.
name|payloadProcessorProvider
operator|.
name|getDirProcessor
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
return|return
name|docBase
return|;
block|}
DECL|method|mergeTerms
specifier|private
specifier|final
name|void
name|mergeTerms
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|Fields
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Fields
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|r
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
decl_stmt|;
specifier|final
name|Fields
name|f
init|=
name|r
operator|.
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|r
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|slices
operator|.
name|add
argument_list|(
operator|new
name|ReaderUtil
operator|.
name|Slice
argument_list|(
name|docBase
argument_list|,
name|maxDoc
argument_list|,
name|readerIndex
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|docBase
operator|+=
name|maxDoc
expr_stmt|;
block|}
specifier|final
name|FieldsConsumer
name|consumer
init|=
name|codec
operator|.
name|postingsFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
operator|new
name|MultiFields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
name|Fields
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices
operator|.
name|toArray
argument_list|(
name|ReaderUtil
operator|.
name|Slice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
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
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergePerDoc
specifier|private
name|void
name|mergePerDoc
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PerDocConsumer
name|docsConsumer
init|=
name|codec
operator|.
name|docValuesFormat
argument_list|()
operator|.
name|docsConsumer
argument_list|(
operator|new
name|PerDocWriteState
argument_list|(
name|segmentWriteState
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: remove this check when 3.x indexes are no longer supported
comment|// (3.x indexes don't have docvalues)
if|if
condition|(
name|docsConsumer
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|docsConsumer
operator|.
name|merge
argument_list|(
name|mergeState
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
name|docsConsumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|docsConsumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeNorms
specifier|private
name|void
name|mergeNorms
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PerDocConsumer
name|docsConsumer
init|=
name|codec
operator|.
name|normsFormat
argument_list|()
operator|.
name|docsConsumer
argument_list|(
operator|new
name|PerDocWriteState
argument_list|(
name|segmentWriteState
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: remove this check when 3.x indexes are no longer supported
comment|// (3.x indexes don't have docvalues)
if|if
condition|(
name|docsConsumer
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|docsConsumer
operator|.
name|merge
argument_list|(
name|mergeState
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
name|docsConsumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|docsConsumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

