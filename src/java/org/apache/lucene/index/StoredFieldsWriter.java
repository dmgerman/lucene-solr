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
name|ArrayUtil
import|;
end_import

begin_comment
comment|/** This is a DocFieldConsumer that writes stored fields. */
end_comment

begin_class
DECL|class|StoredFieldsWriter
specifier|final
class|class
name|StoredFieldsWriter
block|{
DECL|field|fieldsWriter
name|FieldsWriter
name|fieldsWriter
decl_stmt|;
DECL|field|docWriter
specifier|final
name|DocumentsWriter
name|docWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|docFreeList
name|PerDoc
index|[]
name|docFreeList
init|=
operator|new
name|PerDoc
index|[
literal|1
index|]
decl_stmt|;
DECL|field|freeCount
name|int
name|freeCount
decl_stmt|;
DECL|method|StoredFieldsWriter
specifier|public
name|StoredFieldsWriter
parameter_list|(
name|DocumentsWriter
name|docWriter
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
block|}
DECL|method|addThread
specifier|public
name|StoredFieldsWriterPerThread
name|addThread
parameter_list|(
name|DocumentsWriter
operator|.
name|DocState
name|docState
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StoredFieldsWriterPerThread
argument_list|(
name|docState
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|flush
specifier|synchronized
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|.
name|numDocsInStore
operator|>
literal|0
condition|)
block|{
comment|// It's possible that all documents seen in this segment
comment|// hit non-aborting exceptions, in which case we will
comment|// not have yet init'd the FieldsWriter:
name|initFieldsWriter
argument_list|()
expr_stmt|;
comment|// Fill fdx file to include any final docs that we
comment|// skipped because they hit non-aborting exceptions
name|fill
argument_list|(
name|state
operator|.
name|numDocsInStore
operator|-
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldsWriter
operator|!=
literal|null
condition|)
name|fieldsWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|initFieldsWriter
specifier|private
name|void
name|initFieldsWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldsWriter
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|docStoreSegment
init|=
name|docWriter
operator|.
name|getDocStoreSegment
argument_list|()
decl_stmt|;
if|if
condition|(
name|docStoreSegment
operator|!=
literal|null
condition|)
block|{
assert|assert
name|docStoreSegment
operator|!=
literal|null
assert|;
name|fieldsWriter
operator|=
operator|new
name|FieldsWriter
argument_list|(
name|docWriter
operator|.
name|directory
argument_list|,
name|docStoreSegment
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|addOpenFile
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|addOpenFile
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
DECL|method|closeDocStore
specifier|synchronized
specifier|public
name|void
name|closeDocStore
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|inc
init|=
name|state
operator|.
name|numDocsInStore
operator|-
name|lastDocID
decl_stmt|;
if|if
condition|(
name|inc
operator|>
literal|0
condition|)
block|{
name|initFieldsWriter
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|state
operator|.
name|numDocsInStore
operator|-
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldsWriter
operator|!=
literal|null
condition|)
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|fieldsWriter
operator|=
literal|null
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
assert|assert
name|state
operator|.
name|docStoreSegmentName
operator|!=
literal|null
assert|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|state
operator|.
name|docWriter
operator|.
name|removeOpenFile
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|state
operator|.
name|docWriter
operator|.
name|removeOpenFile
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fileName
init|=
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
decl_stmt|;
if|if
condition|(
literal|4
operator|+
operator|(
operator|(
name|long
operator|)
name|state
operator|.
name|numDocsInStore
operator|)
operator|*
literal|8
operator|!=
name|state
operator|.
name|directory
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"after flush: fdx size mismatch: "
operator|+
name|state
operator|.
name|numDocsInStore
operator|+
literal|" docs vs "
operator|+
name|state
operator|.
name|directory
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
operator|+
literal|" length in bytes of "
operator|+
name|fileName
operator|+
literal|" file exists?="
operator|+
name|state
operator|.
name|directory
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|field|allocCount
name|int
name|allocCount
decl_stmt|;
DECL|method|getPerDoc
specifier|synchronized
name|PerDoc
name|getPerDoc
parameter_list|()
block|{
if|if
condition|(
name|freeCount
operator|==
literal|0
condition|)
block|{
name|allocCount
operator|++
expr_stmt|;
if|if
condition|(
name|allocCount
operator|>
name|docFreeList
operator|.
name|length
condition|)
block|{
comment|// Grow our free list up front to make sure we have
comment|// enough space to recycle all outstanding PerDoc
comment|// instances
assert|assert
name|allocCount
operator|==
literal|1
operator|+
name|docFreeList
operator|.
name|length
assert|;
name|docFreeList
operator|=
operator|new
name|PerDoc
index|[
name|ArrayUtil
operator|.
name|getNextSize
argument_list|(
name|allocCount
argument_list|)
index|]
expr_stmt|;
block|}
return|return
operator|new
name|PerDoc
argument_list|()
return|;
block|}
else|else
return|return
name|docFreeList
index|[
operator|--
name|freeCount
index|]
return|;
block|}
DECL|method|abort
specifier|synchronized
name|void
name|abort
parameter_list|()
block|{
if|if
condition|(
name|fieldsWriter
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
name|fieldsWriter
operator|=
literal|null
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** Fills in any hole in the docIDs */
DECL|method|fill
name|void
name|fill
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docStoreOffset
init|=
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
decl_stmt|;
comment|// We must "catch up" for all docs before us
comment|// that had no stored fields:
specifier|final
name|int
name|end
init|=
name|docID
operator|+
name|docStoreOffset
decl_stmt|;
while|while
condition|(
name|lastDocID
operator|<
name|end
condition|)
block|{
name|fieldsWriter
operator|.
name|skipDocument
argument_list|()
expr_stmt|;
name|lastDocID
operator|++
expr_stmt|;
block|}
block|}
DECL|method|finishDocument
specifier|synchronized
name|void
name|finishDocument
parameter_list|(
name|PerDoc
name|perDoc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"StoredFieldsWriter.finishDocument start"
argument_list|)
assert|;
name|initFieldsWriter
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|perDoc
operator|.
name|docID
argument_list|)
expr_stmt|;
comment|// Append stored fields to the real FieldsWriter:
name|fieldsWriter
operator|.
name|flushDocument
argument_list|(
name|perDoc
operator|.
name|numStoredFields
argument_list|,
name|perDoc
operator|.
name|fdt
argument_list|)
expr_stmt|;
name|lastDocID
operator|++
expr_stmt|;
name|perDoc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|free
argument_list|(
name|perDoc
argument_list|)
expr_stmt|;
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"StoredFieldsWriter.finishDocument end"
argument_list|)
assert|;
block|}
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|free
specifier|synchronized
name|void
name|free
parameter_list|(
name|PerDoc
name|perDoc
parameter_list|)
block|{
assert|assert
name|freeCount
operator|<
name|docFreeList
operator|.
name|length
assert|;
assert|assert
literal|0
operator|==
name|perDoc
operator|.
name|numStoredFields
assert|;
assert|assert
literal|0
operator|==
name|perDoc
operator|.
name|fdt
operator|.
name|length
argument_list|()
assert|;
assert|assert
literal|0
operator|==
name|perDoc
operator|.
name|fdt
operator|.
name|getFilePointer
argument_list|()
assert|;
name|docFreeList
index|[
name|freeCount
operator|++
index|]
operator|=
name|perDoc
expr_stmt|;
block|}
DECL|class|PerDoc
class|class
name|PerDoc
extends|extends
name|DocumentsWriter
operator|.
name|DocWriter
block|{
comment|// TODO: use something more memory efficient; for small
comment|// docs the 1024 buffer size of RAMOutputStream wastes alot
DECL|field|fdt
name|RAMOutputStream
name|fdt
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|numStoredFields
name|int
name|numStoredFields
decl_stmt|;
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|fdt
operator|.
name|reset
argument_list|()
expr_stmt|;
name|numStoredFields
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
name|free
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|fdt
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|finishDocument
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

