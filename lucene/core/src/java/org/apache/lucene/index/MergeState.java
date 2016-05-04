begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DocValuesProducer
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
name|NormsProducer
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
name|PointsReader
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
name|StoredFieldsReader
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
name|TermVectorsReader
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
name|search
operator|.
name|Sort
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
name|packed
operator|.
name|PackedInts
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
name|packed
operator|.
name|PackedLongValues
import|;
end_import

begin_comment
comment|/** Holds common state used during segment merging.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|MergeState
specifier|public
class|class
name|MergeState
block|{
comment|/** Maps document IDs from old segments to document IDs in the new segment */
comment|// nocommit in the unsorted case, this should map correctly, e.g. apply per segment docBase
DECL|field|docMaps
specifier|public
specifier|final
name|DocMap
index|[]
name|docMaps
decl_stmt|;
comment|// nocommit can we somehow not need to expose this?  should IW's reader pool always sort on load...?
DECL|field|leafDocMaps
specifier|public
specifier|final
name|DocMap
index|[]
name|leafDocMaps
decl_stmt|;
comment|/** {@link SegmentInfo} of the newly merged segment. */
DECL|field|segmentInfo
specifier|public
specifier|final
name|SegmentInfo
name|segmentInfo
decl_stmt|;
comment|/** {@link FieldInfos} of the newly merged segment. */
DECL|field|mergeFieldInfos
specifier|public
name|FieldInfos
name|mergeFieldInfos
decl_stmt|;
comment|/** Stored field producers being merged */
DECL|field|storedFieldsReaders
specifier|public
specifier|final
name|StoredFieldsReader
index|[]
name|storedFieldsReaders
decl_stmt|;
comment|/** Term vector producers being merged */
DECL|field|termVectorsReaders
specifier|public
specifier|final
name|TermVectorsReader
index|[]
name|termVectorsReaders
decl_stmt|;
comment|/** Norms producers being merged */
DECL|field|normsProducers
specifier|public
specifier|final
name|NormsProducer
index|[]
name|normsProducers
decl_stmt|;
comment|/** DocValues producers being merged */
DECL|field|docValuesProducers
specifier|public
specifier|final
name|DocValuesProducer
index|[]
name|docValuesProducers
decl_stmt|;
comment|/** FieldInfos being merged */
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
index|[]
name|fieldInfos
decl_stmt|;
comment|/** Live docs for each reader */
DECL|field|liveDocs
specifier|public
specifier|final
name|Bits
index|[]
name|liveDocs
decl_stmt|;
comment|/** Postings to merge */
DECL|field|fieldsProducers
specifier|public
specifier|final
name|FieldsProducer
index|[]
name|fieldsProducers
decl_stmt|;
comment|/** Point readers to merge */
DECL|field|pointsReaders
specifier|public
specifier|final
name|PointsReader
index|[]
name|pointsReaders
decl_stmt|;
comment|/** Max docs per reader */
DECL|field|maxDocs
specifier|public
specifier|final
name|int
index|[]
name|maxDocs
decl_stmt|;
comment|/** InfoStream for debugging messages. */
DECL|field|infoStream
specifier|public
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MergeState
name|MergeState
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|originalReaders
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|InfoStream
name|infoStream
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Sort
name|indexSort
init|=
name|segmentInfo
operator|.
name|getIndexSort
argument_list|()
decl_stmt|;
name|int
name|numReaders
init|=
name|originalReaders
operator|.
name|size
argument_list|()
decl_stmt|;
name|leafDocMaps
operator|=
operator|new
name|DocMap
index|[
name|numReaders
index|]
expr_stmt|;
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
init|=
name|maybeSortReaders
argument_list|(
name|originalReaders
argument_list|,
name|segmentInfo
argument_list|)
decl_stmt|;
name|maxDocs
operator|=
operator|new
name|int
index|[
name|numReaders
index|]
expr_stmt|;
name|fieldsProducers
operator|=
operator|new
name|FieldsProducer
index|[
name|numReaders
index|]
expr_stmt|;
name|normsProducers
operator|=
operator|new
name|NormsProducer
index|[
name|numReaders
index|]
expr_stmt|;
name|storedFieldsReaders
operator|=
operator|new
name|StoredFieldsReader
index|[
name|numReaders
index|]
expr_stmt|;
name|termVectorsReaders
operator|=
operator|new
name|TermVectorsReader
index|[
name|numReaders
index|]
expr_stmt|;
name|docValuesProducers
operator|=
operator|new
name|DocValuesProducer
index|[
name|numReaders
index|]
expr_stmt|;
name|pointsReaders
operator|=
operator|new
name|PointsReader
index|[
name|numReaders
index|]
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
index|[
name|numReaders
index|]
expr_stmt|;
name|liveDocs
operator|=
operator|new
name|Bits
index|[
name|numReaders
index|]
expr_stmt|;
name|int
name|numDocs
init|=
literal|0
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
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CodecReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|maxDocs
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|liveDocs
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|fieldInfos
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
expr_stmt|;
name|normsProducers
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getNormsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|normsProducers
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|normsProducers
index|[
name|i
index|]
operator|=
name|normsProducers
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|docValuesProducers
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getDocValuesReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|docValuesProducers
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|docValuesProducers
index|[
name|i
index|]
operator|=
name|docValuesProducers
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|storedFieldsReaders
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getFieldsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|storedFieldsReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|storedFieldsReaders
index|[
name|i
index|]
operator|=
name|storedFieldsReaders
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|termVectorsReaders
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getTermVectorsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|termVectorsReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|termVectorsReaders
index|[
name|i
index|]
operator|=
name|termVectorsReaders
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|fieldsProducers
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getPostingsReader
argument_list|()
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
name|pointsReaders
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getPointsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|pointsReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|pointsReaders
index|[
name|i
index|]
operator|=
name|pointsReaders
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|numDocs
operator|+=
name|reader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
block|}
name|segmentInfo
operator|.
name|setMaxDoc
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentInfo
operator|=
name|segmentInfo
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|this
operator|.
name|docMaps
operator|=
name|buildDocMaps
argument_list|(
name|readers
argument_list|,
name|indexSort
argument_list|)
expr_stmt|;
block|}
DECL|method|buildDocMaps
specifier|private
name|DocMap
index|[]
name|buildDocMaps
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|,
name|Sort
name|indexSort
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numReaders
init|=
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexSort
operator|==
literal|null
condition|)
block|{
comment|// no index sort ... we only must map around deletions, and rebase to the merged segment's docID space
name|int
name|totalDocs
init|=
literal|0
decl_stmt|;
name|DocMap
index|[]
name|docMaps
init|=
operator|new
name|DocMap
index|[
name|numReaders
index|]
decl_stmt|;
comment|// Remap docIDs around deletions:
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
name|LeafReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
specifier|final
name|PackedLongValues
name|delDocMap
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
name|delDocMap
operator|=
name|removeDeletes
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|liveDocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delDocMap
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|int
name|docBase
init|=
name|totalDocs
decl_stmt|;
name|docMaps
index|[
name|i
index|]
operator|=
operator|new
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
return|return
name|docBase
operator|+
name|docID
return|;
block|}
elseif|else
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
name|docBase
operator|+
operator|(
name|int
operator|)
name|delDocMap
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
expr_stmt|;
name|totalDocs
operator|+=
name|reader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
block|}
return|return
name|docMaps
return|;
block|}
else|else
block|{
comment|// do a merge sort of the incoming leaves:
return|return
name|MultiSorter
operator|.
name|sort
argument_list|(
name|indexSort
argument_list|,
name|readers
argument_list|)
return|;
block|}
block|}
DECL|method|maybeSortReaders
specifier|private
name|List
argument_list|<
name|CodecReader
argument_list|>
name|maybeSortReaders
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|originalReaders
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Default to identity:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|originalReaders
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|leafDocMaps
index|[
name|i
index|]
operator|=
operator|new
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|docID
return|;
block|}
block|}
expr_stmt|;
block|}
name|Sort
name|indexSort
init|=
name|segmentInfo
operator|.
name|getIndexSort
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexSort
operator|==
literal|null
condition|)
block|{
return|return
name|originalReaders
return|;
block|}
comment|// If an incoming reader is not sorted, because it was flushed by IW, we sort it here:
specifier|final
name|Sorter
name|sorter
init|=
operator|new
name|Sorter
argument_list|(
name|indexSort
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|originalReaders
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("MergeState.maybeSortReaders indexSort=" + indexSort);
for|for
control|(
name|CodecReader
name|leaf
range|:
name|originalReaders
control|)
block|{
if|if
condition|(
name|leaf
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
name|leaf
decl_stmt|;
name|Sort
name|segmentSort
init|=
name|segmentReader
operator|.
name|getSegmentInfo
argument_list|()
operator|.
name|info
operator|.
name|getIndexSort
argument_list|()
decl_stmt|;
comment|//System.out.println("  leaf=" + leaf + " sort=" + segmentSort);
if|if
condition|(
name|segmentSort
operator|==
literal|null
condition|)
block|{
comment|// TODO: fix IW to also sort when flushing?  It's somewhat tricky because of stored fields and term vectors, which write "live"
comment|// to the files on each indexed document:
comment|// This segment was written by flush, so documents are not yet sorted, so we sort them now:
name|Sorter
operator|.
name|DocMap
name|sortDocMap
init|=
name|sorter
operator|.
name|sort
argument_list|(
name|leaf
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortDocMap
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("    sort!");
comment|// nocommit what about MergedReaderWrapper in here?
name|leaf
operator|=
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
name|SortingLeafReader
operator|.
name|wrap
argument_list|(
name|leaf
argument_list|,
name|sortDocMap
argument_list|)
argument_list|)
expr_stmt|;
name|leafDocMaps
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
operator|=
operator|new
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|sortDocMap
operator|.
name|oldToNew
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|segmentSort
operator|.
name|equals
argument_list|(
name|indexSort
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"index sort mismatch: merged segment has sort="
operator|+
name|indexSort
operator|+
literal|" but to-be-merged segment has sort="
operator|+
name|segmentSort
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot sort index with foreign readers; leaf="
operator|+
name|leaf
argument_list|)
throw|;
block|}
name|readers
operator|.
name|add
argument_list|(
name|leaf
argument_list|)
expr_stmt|;
block|}
return|return
name|readers
return|;
block|}
comment|/** A map of doc IDs. */
DECL|class|DocMap
specifier|public
specifier|static
specifier|abstract
class|class
name|DocMap
block|{
comment|/** Return the mapped docID or -1 if the given doc is not mapped. */
DECL|method|get
specifier|public
specifier|abstract
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
block|}
DECL|method|removeDeletes
specifier|static
name|PackedLongValues
name|removeDeletes
parameter_list|(
specifier|final
name|int
name|maxDoc
parameter_list|,
specifier|final
name|Bits
name|liveDocs
parameter_list|)
block|{
specifier|final
name|PackedLongValues
operator|.
name|Builder
name|docMapBuilder
init|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|int
name|del
init|=
literal|0
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|docMapBuilder
operator|.
name|add
argument_list|(
name|i
operator|-
name|del
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|false
condition|)
block|{
operator|++
name|del
expr_stmt|;
block|}
block|}
return|return
name|docMapBuilder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

