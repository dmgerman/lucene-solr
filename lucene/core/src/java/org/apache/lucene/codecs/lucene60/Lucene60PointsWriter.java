begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene60
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene60
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|PointsWriter
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
name|MergeState
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
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|bkd
operator|.
name|BKDReader
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
name|bkd
operator|.
name|BKDWriter
import|;
end_import

begin_comment
comment|/** Writes dimensional values */
end_comment

begin_class
DECL|class|Lucene60PointsWriter
specifier|public
class|class
name|Lucene60PointsWriter
extends|extends
name|PointsWriter
implements|implements
name|Closeable
block|{
DECL|field|dataOut
specifier|final
name|IndexOutput
name|dataOut
decl_stmt|;
DECL|field|indexFPs
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|indexFPs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|writeState
specifier|final
name|SegmentWriteState
name|writeState
decl_stmt|;
DECL|field|maxPointsInLeafNode
specifier|final
name|int
name|maxPointsInLeafNode
decl_stmt|;
DECL|field|maxMBSortInHeap
specifier|final
name|double
name|maxMBSortInHeap
decl_stmt|;
DECL|field|finished
specifier|private
name|boolean
name|finished
decl_stmt|;
comment|/** Full constructor */
DECL|method|Lucene60PointsWriter
specifier|public
name|Lucene60PointsWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|,
name|int
name|maxPointsInLeafNode
parameter_list|,
name|double
name|maxMBSortInHeap
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|writeState
operator|.
name|fieldInfos
operator|.
name|hasPointValues
argument_list|()
assert|;
name|this
operator|.
name|writeState
operator|=
name|writeState
expr_stmt|;
name|this
operator|.
name|maxPointsInLeafNode
operator|=
name|maxPointsInLeafNode
expr_stmt|;
name|this
operator|.
name|maxMBSortInHeap
operator|=
name|maxMBSortInHeap
expr_stmt|;
name|String
name|dataFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|writeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|writeState
operator|.
name|segmentSuffix
argument_list|,
name|Lucene60PointsFormat
operator|.
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
name|dataOut
operator|=
name|writeState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|dataFileName
argument_list|,
name|writeState
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
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|dataOut
argument_list|,
name|Lucene60PointsFormat
operator|.
name|DATA_CODEC_NAME
argument_list|,
name|Lucene60PointsFormat
operator|.
name|DATA_VERSION_CURRENT
argument_list|,
name|writeState
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|writeState
operator|.
name|segmentSuffix
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
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Uses the defaults values for {@code maxPointsInLeafNode} (1024) and {@code maxMBSortInHeap} (16.0) */
DECL|method|Lucene60PointsWriter
specifier|public
name|Lucene60PointsWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|writeState
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_POINTS_IN_LEAF_NODE
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_MB_SORT_IN_HEAP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|PointsReader
name|values
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BKDWriter
name|writer
init|=
operator|new
name|BKDWriter
argument_list|(
name|writeState
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|writeState
operator|.
name|directory
argument_list|,
name|writeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
argument_list|,
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|maxMBSortInHeap
argument_list|,
name|values
operator|.
name|size
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
argument_list|)
init|)
block|{
name|values
operator|.
name|intersect
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|add
argument_list|(
name|packedValue
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// We could have 0 points on merge since all docs with dimensional fields may be deleted:
if|if
condition|(
name|writer
operator|.
name|getPointCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|indexFPs
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|writer
operator|.
name|finish
argument_list|(
name|dataOut
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|PointsReader
name|reader
range|:
name|mergeState
operator|.
name|pointsReaders
control|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|Lucene60PointsReader
operator|==
literal|false
condition|)
block|{
comment|// We can only bulk merge when all to-be-merged segments use our format:
name|super
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
for|for
control|(
name|PointsReader
name|reader
range|:
name|mergeState
operator|.
name|pointsReaders
control|)
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|mergeState
operator|.
name|mergeFieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// Worst case total maximum size (if none of the points are deleted):
name|long
name|totMaxSize
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
name|mergeState
operator|.
name|pointsReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PointsReader
name|reader
init|=
name|mergeState
operator|.
name|pointsReaders
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|FieldInfos
name|readerFieldInfos
init|=
name|mergeState
operator|.
name|fieldInfos
index|[
name|i
index|]
decl_stmt|;
name|FieldInfo
name|readerFieldInfo
init|=
name|readerFieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerFieldInfo
operator|!=
literal|null
condition|)
block|{
name|totMaxSize
operator|+=
name|reader
operator|.
name|size
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//System.out.println("MERGE: field=" + fieldInfo.name);
comment|// Optimize the 1D case to use BKDWriter.merge, which does a single merge sort of the
comment|// already sorted incoming segments, instead of trying to sort all points again as if
comment|// we were simply reindexing them:
try|try
init|(
name|BKDWriter
name|writer
init|=
operator|new
name|BKDWriter
argument_list|(
name|writeState
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|writeState
operator|.
name|directory
argument_list|,
name|writeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
argument_list|,
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|maxMBSortInHeap
argument_list|,
name|totMaxSize
argument_list|)
init|)
block|{
name|List
argument_list|<
name|BKDReader
argument_list|>
name|bkdReaders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MergeState
operator|.
name|DocMap
argument_list|>
name|docMaps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|docIDBases
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|mergeState
operator|.
name|pointsReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PointsReader
name|reader
init|=
name|mergeState
operator|.
name|pointsReaders
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
comment|// we confirmed this up above
assert|assert
name|reader
operator|instanceof
name|Lucene60PointsReader
assert|;
name|Lucene60PointsReader
name|reader60
init|=
operator|(
name|Lucene60PointsReader
operator|)
name|reader
decl_stmt|;
comment|// NOTE: we cannot just use the merged fieldInfo.number (instead of resolving to this
comment|// reader's FieldInfo as we do below) because field numbers can easily be different
comment|// when addIndexes(Directory...) copies over segments from another index:
name|FieldInfos
name|readerFieldInfos
init|=
name|mergeState
operator|.
name|fieldInfos
index|[
name|i
index|]
decl_stmt|;
name|FieldInfo
name|readerFieldInfo
init|=
name|readerFieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerFieldInfo
operator|!=
literal|null
condition|)
block|{
name|BKDReader
name|bkdReader
init|=
name|reader60
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerFieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|bkdReader
operator|!=
literal|null
condition|)
block|{
name|docIDBases
operator|.
name|add
argument_list|(
name|mergeState
operator|.
name|docBase
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|bkdReaders
operator|.
name|add
argument_list|(
name|bkdReader
argument_list|)
expr_stmt|;
name|docMaps
operator|.
name|add
argument_list|(
name|mergeState
operator|.
name|docMaps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|long
name|fp
init|=
name|writer
operator|.
name|merge
argument_list|(
name|dataOut
argument_list|,
name|docMaps
argument_list|,
name|bkdReaders
argument_list|,
name|docIDBases
argument_list|)
decl_stmt|;
if|if
condition|(
name|fp
operator|!=
operator|-
literal|1
condition|)
block|{
name|indexFPs
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|fp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|mergeOneField
argument_list|(
name|mergeState
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|finished
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already finished"
argument_list|)
throw|;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|String
name|indexFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|writeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|writeState
operator|.
name|segmentSuffix
argument_list|,
name|Lucene60PointsFormat
operator|.
name|INDEX_EXTENSION
argument_list|)
decl_stmt|;
comment|// Write index file
try|try
init|(
name|IndexOutput
name|indexOut
init|=
name|writeState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|indexFileName
argument_list|,
name|writeState
operator|.
name|context
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|indexOut
argument_list|,
name|Lucene60PointsFormat
operator|.
name|META_CODEC_NAME
argument_list|,
name|Lucene60PointsFormat
operator|.
name|INDEX_VERSION_CURRENT
argument_list|,
name|writeState
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|writeState
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|indexFPs
operator|.
name|size
argument_list|()
decl_stmt|;
name|indexOut
operator|.
name|writeVInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|ent
range|:
name|indexFPs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FieldInfo
name|fieldInfo
init|=
name|writeState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"wrote field=\""
operator|+
name|ent
operator|.
name|getKey
argument_list|()
operator|+
literal|"\" but that field doesn't exist in FieldInfos"
argument_list|)
throw|;
block|}
name|indexOut
operator|.
name|writeVInt
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|indexOut
operator|.
name|writeVLong
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|indexOut
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
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

