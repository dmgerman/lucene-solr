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
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|DimensionalReader
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
name|DimensionalWriter
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
name|DimensionalValues
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
name|DimensionalValues
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
name|bkd
operator|.
name|BKDWriter
import|;
end_import

begin_class
DECL|class|SimpleTextDimensionalWriter
class|class
name|SimpleTextDimensionalWriter
extends|extends
name|DimensionalWriter
block|{
DECL|field|NUM_DIMS
specifier|final
specifier|static
name|BytesRef
name|NUM_DIMS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"num dims "
argument_list|)
decl_stmt|;
DECL|field|BYTES_PER_DIM
specifier|final
specifier|static
name|BytesRef
name|BYTES_PER_DIM
init|=
operator|new
name|BytesRef
argument_list|(
literal|"bytes per dim "
argument_list|)
decl_stmt|;
DECL|field|MAX_LEAF_POINTS
specifier|final
specifier|static
name|BytesRef
name|MAX_LEAF_POINTS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"max leaf points "
argument_list|)
decl_stmt|;
DECL|field|INDEX_COUNT
specifier|final
specifier|static
name|BytesRef
name|INDEX_COUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"index count "
argument_list|)
decl_stmt|;
DECL|field|BLOCK_COUNT
specifier|final
specifier|static
name|BytesRef
name|BLOCK_COUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"block count "
argument_list|)
decl_stmt|;
DECL|field|BLOCK_DOC_ID
specifier|final
specifier|static
name|BytesRef
name|BLOCK_DOC_ID
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  doc "
argument_list|)
decl_stmt|;
DECL|field|BLOCK_FP
specifier|final
specifier|static
name|BytesRef
name|BLOCK_FP
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  block fp "
argument_list|)
decl_stmt|;
DECL|field|BLOCK_VALUE
specifier|final
specifier|static
name|BytesRef
name|BLOCK_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  block value "
argument_list|)
decl_stmt|;
DECL|field|SPLIT_COUNT
specifier|final
specifier|static
name|BytesRef
name|SPLIT_COUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"split count "
argument_list|)
decl_stmt|;
DECL|field|SPLIT_DIM
specifier|final
specifier|static
name|BytesRef
name|SPLIT_DIM
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  split dim "
argument_list|)
decl_stmt|;
DECL|field|SPLIT_VALUE
specifier|final
specifier|static
name|BytesRef
name|SPLIT_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  split value "
argument_list|)
decl_stmt|;
DECL|field|FIELD_COUNT
specifier|final
specifier|static
name|BytesRef
name|FIELD_COUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"field count "
argument_list|)
decl_stmt|;
DECL|field|FIELD_FP_NAME
specifier|final
specifier|static
name|BytesRef
name|FIELD_FP_NAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  field fp name "
argument_list|)
decl_stmt|;
DECL|field|FIELD_FP
specifier|final
specifier|static
name|BytesRef
name|FIELD_FP
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  field fp "
argument_list|)
decl_stmt|;
DECL|field|dataOut
specifier|private
name|IndexOutput
name|dataOut
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|writeState
specifier|final
name|SegmentWriteState
name|writeState
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
DECL|method|SimpleTextDimensionalWriter
specifier|public
name|SimpleTextDimensionalWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileName
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
name|SimpleTextDimensionalFormat
operator|.
name|DIMENSIONAL_EXTENSION
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
name|fileName
argument_list|,
name|writeState
operator|.
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeState
operator|=
name|writeState
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
name|DimensionalReader
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We use the normal BKDWriter, but subclass to customize how it writes the index and blocks to disk:
name|BKDWriter
name|writer
init|=
operator|new
name|BKDWriter
argument_list|(
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
name|getDimensionCount
argument_list|()
argument_list|,
name|fieldInfo
operator|.
name|getDimensionNumBytes
argument_list|()
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_POINTS_IN_LEAF_NODE
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_MB_SORT_IN_HEAP
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|writeIndex
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
index|[]
name|leafBlockFPs
parameter_list|,
name|byte
index|[]
name|splitPackedValues
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|out
argument_list|,
name|NUM_DIMS
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|numDims
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|BYTES_PER_DIM
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|MAX_LEAF_POINTS
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|maxPointsInLeafNode
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|INDEX_COUNT
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|leafBlockFPs
operator|.
name|length
argument_list|)
expr_stmt|;
name|newline
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
name|leafBlockFPs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|write
argument_list|(
name|out
argument_list|,
name|BLOCK_FP
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
name|out
argument_list|,
name|leafBlockFPs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|(
name|splitPackedValues
operator|.
name|length
operator|%
operator|(
literal|1
operator|+
name|fieldInfo
operator|.
name|getDimensionNumBytes
argument_list|()
operator|)
operator|)
operator|==
literal|0
assert|;
name|int
name|count
init|=
name|splitPackedValues
operator|.
name|length
operator|/
operator|(
literal|1
operator|+
name|fieldInfo
operator|.
name|getDimensionNumBytes
argument_list|()
operator|)
decl_stmt|;
assert|assert
name|count
operator|==
name|leafBlockFPs
operator|.
name|length
assert|;
name|write
argument_list|(
name|out
argument_list|,
name|SPLIT_COUNT
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|newline
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|write
argument_list|(
name|out
argument_list|,
name|SPLIT_DIM
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|splitPackedValues
index|[
name|i
operator|*
operator|(
literal|1
operator|+
name|fieldInfo
operator|.
name|getDimensionNumBytes
argument_list|()
operator|)
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|SPLIT_VALUE
argument_list|)
expr_stmt|;
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|(
name|splitPackedValues
argument_list|,
literal|1
operator|+
operator|(
name|i
operator|*
operator|(
literal|1
operator|+
name|fieldInfo
operator|.
name|getDimensionNumBytes
argument_list|()
operator|)
operator|)
argument_list|,
name|fieldInfo
operator|.
name|getDimensionNumBytes
argument_list|()
argument_list|)
decl_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|br
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeLeafBlockDocs
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|out
argument_list|,
name|BLOCK_COUNT
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|newline
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|write
argument_list|(
name|out
argument_list|,
name|BLOCK_DOC_ID
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
name|docIDs
index|[
name|start
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeLeafBlockPackedValue
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|length
operator|==
name|packedBytesLength
assert|;
name|write
argument_list|(
name|out
argument_list|,
name|BLOCK_VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
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
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|s
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|writeInt
specifier|private
name|void
name|writeInt
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|int
name|x
parameter_list|)
throws|throws
name|IOException
block|{
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
name|x
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|writeLong
specifier|private
name|void
name|writeLong
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
name|x
parameter_list|)
throws|throws
name|IOException
block|{
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
name|x
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|BytesRef
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|newline
specifier|private
name|void
name|newline
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|dataOut
operator|!=
literal|null
condition|)
block|{
name|SimpleTextUtil
operator|.
name|writeChecksum
argument_list|(
name|dataOut
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|dataOut
operator|=
literal|null
expr_stmt|;
comment|// Write index file
name|String
name|fileName
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
name|SimpleTextDimensionalFormat
operator|.
name|DIMENSIONAL_INDEX_EXTENSION
argument_list|)
decl_stmt|;
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
name|fileName
argument_list|,
name|writeState
operator|.
name|context
argument_list|)
init|)
block|{
name|int
name|count
init|=
name|indexFPs
operator|.
name|size
argument_list|()
decl_stmt|;
name|write
argument_list|(
name|indexOut
argument_list|,
name|FIELD_COUNT
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|indexOut
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|count
argument_list|)
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|indexOut
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
name|write
argument_list|(
name|indexOut
argument_list|,
name|FIELD_FP_NAME
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|indexOut
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|indexOut
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|indexOut
argument_list|,
name|FIELD_FP
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|indexOut
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|indexOut
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|writeChecksum
argument_list|(
name|indexOut
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

