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
name|Collection
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
name|HashSet
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
name|ArrayUtil
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
name|Counter
import|;
end_import

begin_comment
comment|/**  * This is a DocConsumer that gathers all fields under the  * same name, and calls per-field consumers to process field  * by field.  This class doesn't doesn't do any "real" work  * of its own: it just forwards the fields to a  * DocFieldConsumer.  */
end_comment

begin_class
DECL|class|DocFieldProcessor
specifier|final
class|class
name|DocFieldProcessor
extends|extends
name|DocConsumer
block|{
DECL|field|consumer
specifier|final
name|DocFieldConsumer
name|consumer
decl_stmt|;
DECL|field|storedConsumer
specifier|final
name|StoredFieldsConsumer
name|storedConsumer
decl_stmt|;
DECL|field|codec
specifier|final
name|Codec
name|codec
decl_stmt|;
comment|// Holds all fields seen in current doc
DECL|field|fields
name|DocFieldProcessorPerField
index|[]
name|fields
init|=
operator|new
name|DocFieldProcessorPerField
index|[
literal|1
index|]
decl_stmt|;
DECL|field|fieldCount
name|int
name|fieldCount
decl_stmt|;
comment|// Hash table for all fields ever seen
DECL|field|fieldHash
name|DocFieldProcessorPerField
index|[]
name|fieldHash
init|=
operator|new
name|DocFieldProcessorPerField
index|[
literal|2
index|]
decl_stmt|;
DECL|field|hashMask
name|int
name|hashMask
init|=
literal|1
decl_stmt|;
DECL|field|totalFieldCount
name|int
name|totalFieldCount
decl_stmt|;
DECL|field|fieldGen
name|int
name|fieldGen
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|bytesUsed
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|method|DocFieldProcessor
specifier|public
name|DocFieldProcessor
parameter_list|(
name|DocumentsWriterPerThread
name|docWriter
parameter_list|,
name|DocFieldConsumer
name|consumer
parameter_list|,
name|StoredFieldsConsumer
name|storedConsumer
parameter_list|)
block|{
name|this
operator|.
name|docState
operator|=
name|docWriter
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|docWriter
operator|.
name|codec
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|docWriter
operator|.
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|storedConsumer
operator|=
name|storedConsumer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
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
name|Map
argument_list|<
name|String
argument_list|,
name|DocFieldConsumerPerField
argument_list|>
name|childFields
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocFieldConsumerPerField
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
for|for
control|(
name|DocFieldConsumerPerField
name|f
range|:
name|fields
control|)
block|{
name|childFields
operator|.
name|put
argument_list|(
name|f
operator|.
name|getFieldInfo
argument_list|()
operator|.
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
assert|assert
name|fields
operator|.
name|size
argument_list|()
operator|==
name|totalFieldCount
assert|;
name|storedConsumer
operator|.
name|flush
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|flush
argument_list|(
name|childFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
comment|// Important to save after asking consumer to flush so
comment|// consumer can alter the FieldInfo* if necessary.  EG,
comment|// FreqProxTermsWriter does this with
comment|// FieldInfo.storePayload.
name|FieldInfosWriter
name|infosWriter
init|=
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosWriter
argument_list|()
decl_stmt|;
name|infosWriter
operator|.
name|write
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DocFieldProcessorPerField
name|field
range|:
name|fieldHash
control|)
block|{
while|while
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocFieldProcessorPerField
name|next
init|=
name|field
operator|.
name|next
decl_stmt|;
try|try
block|{
name|field
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
name|field
operator|=
name|next
expr_stmt|;
block|}
block|}
try|try
block|{
name|storedConsumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
try|try
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
comment|// If any errors occured, throw it.
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
comment|// defensive code - we should not hit unchecked exceptions
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
DECL|method|fields
specifier|public
name|Collection
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
name|fields
parameter_list|()
block|{
name|Collection
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
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
name|fieldHash
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocFieldProcessorPerField
name|field
init|=
name|fieldHash
index|[
name|i
index|]
decl_stmt|;
while|while
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
operator|.
name|consumer
argument_list|)
expr_stmt|;
name|field
operator|=
name|field
operator|.
name|next
expr_stmt|;
block|}
block|}
assert|assert
name|fields
operator|.
name|size
argument_list|()
operator|==
name|totalFieldCount
assert|;
return|return
name|fields
return|;
block|}
comment|/** In flush we reset the fieldHash to not maintain per-field state    *  across segments */
annotation|@
name|Override
DECL|method|doAfterFlush
name|void
name|doAfterFlush
parameter_list|()
block|{
name|fieldHash
operator|=
operator|new
name|DocFieldProcessorPerField
index|[
literal|2
index|]
expr_stmt|;
name|hashMask
operator|=
literal|1
expr_stmt|;
name|totalFieldCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
block|{
specifier|final
name|int
name|newHashSize
init|=
operator|(
name|fieldHash
operator|.
name|length
operator|*
literal|2
operator|)
decl_stmt|;
assert|assert
name|newHashSize
operator|>
name|fieldHash
operator|.
name|length
assert|;
specifier|final
name|DocFieldProcessorPerField
name|newHashArray
index|[]
init|=
operator|new
name|DocFieldProcessorPerField
index|[
name|newHashSize
index|]
decl_stmt|;
comment|// Rehash
name|int
name|newHashMask
init|=
name|newHashSize
operator|-
literal|1
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
name|fieldHash
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|DocFieldProcessorPerField
name|fp0
init|=
name|fieldHash
index|[
name|j
index|]
decl_stmt|;
while|while
condition|(
name|fp0
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|hashPos2
init|=
name|fp0
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|hashCode
argument_list|()
operator|&
name|newHashMask
decl_stmt|;
name|DocFieldProcessorPerField
name|nextFP0
init|=
name|fp0
operator|.
name|next
decl_stmt|;
name|fp0
operator|.
name|next
operator|=
name|newHashArray
index|[
name|hashPos2
index|]
expr_stmt|;
name|newHashArray
index|[
name|hashPos2
index|]
operator|=
name|fp0
expr_stmt|;
name|fp0
operator|=
name|nextFP0
expr_stmt|;
block|}
block|}
name|fieldHash
operator|=
name|newHashArray
expr_stmt|;
name|hashMask
operator|=
name|newHashMask
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processDocument
specifier|public
name|void
name|processDocument
parameter_list|(
name|FieldInfos
operator|.
name|Builder
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|storedConsumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|fieldCount
operator|=
literal|0
expr_stmt|;
specifier|final
name|int
name|thisFieldGen
init|=
name|fieldGen
operator|++
decl_stmt|;
comment|// Absorb any new fields first seen in this document.
comment|// Also absorb any changes to fields we had already
comment|// seen before (eg suddenly turning on norms or
comment|// vectors, etc.):
for|for
control|(
name|IndexableField
name|field
range|:
name|docState
operator|.
name|doc
operator|.
name|indexableFields
argument_list|()
control|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
name|IndexableFieldType
name|ft
init|=
name|field
operator|.
name|fieldType
argument_list|()
decl_stmt|;
name|DocFieldProcessorPerField
name|fp
init|=
name|processField
argument_list|(
name|fieldInfos
argument_list|,
name|thisFieldGen
argument_list|,
name|fieldName
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|fp
operator|.
name|addField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|StorableField
name|field
range|:
name|docState
operator|.
name|doc
operator|.
name|storableFields
argument_list|()
control|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
name|IndexableFieldType
name|ft
init|=
name|field
operator|.
name|fieldType
argument_list|()
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|addOrUpdate
argument_list|(
name|fieldName
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|storedConsumer
operator|.
name|addField
argument_list|(
name|docState
operator|.
name|docID
argument_list|,
name|field
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
comment|// If we are writing vectors then we must visit
comment|// fields in sorted order so they are written in
comment|// sorted order.  TODO: we actually only need to
comment|// sort the subset of fields that have vectors
comment|// enabled; we could save [small amount of] CPU
comment|// here.
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|fields
argument_list|,
literal|0
argument_list|,
name|fieldCount
argument_list|,
name|fieldsComp
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|DocFieldProcessorPerField
name|perField
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|perField
operator|.
name|consumer
operator|.
name|processFields
argument_list|(
name|perField
operator|.
name|fields
argument_list|,
name|perField
operator|.
name|fieldCount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docState
operator|.
name|maxTermPrefix
operator|!=
literal|null
operator|&&
name|docState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"IW"
argument_list|)
condition|)
block|{
name|docState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"IW"
argument_list|,
literal|"WARNING: document contains at least one immense term (whose UTF8 encoding is longer than the max length "
operator|+
name|DocumentsWriterPerThread
operator|.
name|MAX_TERM_LENGTH_UTF8
operator|+
literal|"), all of which were skipped.  Please correct the analyzer to not produce such terms.  The prefix of the first immense term is: '"
operator|+
name|docState
operator|.
name|maxTermPrefix
operator|+
literal|"...'"
argument_list|)
expr_stmt|;
name|docState
operator|.
name|maxTermPrefix
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|processField
specifier|private
name|DocFieldProcessorPerField
name|processField
parameter_list|(
name|FieldInfos
operator|.
name|Builder
name|fieldInfos
parameter_list|,
specifier|final
name|int
name|thisFieldGen
parameter_list|,
specifier|final
name|String
name|fieldName
parameter_list|,
name|IndexableFieldType
name|ft
parameter_list|)
block|{
comment|// Make sure we have a PerField allocated
specifier|final
name|int
name|hashPos
init|=
name|fieldName
operator|.
name|hashCode
argument_list|()
operator|&
name|hashMask
decl_stmt|;
name|DocFieldProcessorPerField
name|fp
init|=
name|fieldHash
index|[
name|hashPos
index|]
decl_stmt|;
while|while
condition|(
name|fp
operator|!=
literal|null
operator|&&
operator|!
name|fp
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fp
operator|=
name|fp
operator|.
name|next
expr_stmt|;
block|}
if|if
condition|(
name|fp
operator|==
literal|null
condition|)
block|{
comment|// TODO FI: we need to genericize the "flags" that a
comment|// field holds, and, how these flags are merged; it
comment|// needs to be more "pluggable" such that if I want
comment|// to have a new "thing" my Fields can do, I can
comment|// easily add it
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|addOrUpdate
argument_list|(
name|fieldName
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|fp
operator|=
operator|new
name|DocFieldProcessorPerField
argument_list|(
name|this
argument_list|,
name|fi
argument_list|)
expr_stmt|;
name|fp
operator|.
name|next
operator|=
name|fieldHash
index|[
name|hashPos
index|]
expr_stmt|;
name|fieldHash
index|[
name|hashPos
index|]
operator|=
name|fp
expr_stmt|;
name|totalFieldCount
operator|++
expr_stmt|;
if|if
condition|(
name|totalFieldCount
operator|>=
name|fieldHash
operator|.
name|length
operator|/
literal|2
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// nocommit this is wasteful: it's another hash lookup
comment|// by field name; can we just do fp.fieldInfo.update
comment|// directly?
name|fieldInfos
operator|.
name|addOrUpdate
argument_list|(
name|fp
operator|.
name|fieldInfo
operator|.
name|name
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|thisFieldGen
operator|!=
name|fp
operator|.
name|lastGen
condition|)
block|{
comment|// First time we're seeing this field for this doc
name|fp
operator|.
name|fieldCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|fieldCount
operator|==
name|fields
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|newSize
init|=
name|fields
operator|.
name|length
operator|*
literal|2
decl_stmt|;
name|DocFieldProcessorPerField
name|newArray
index|[]
init|=
operator|new
name|DocFieldProcessorPerField
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fields
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|fieldCount
argument_list|)
expr_stmt|;
name|fields
operator|=
name|newArray
expr_stmt|;
block|}
name|fields
index|[
name|fieldCount
operator|++
index|]
operator|=
name|fp
expr_stmt|;
name|fp
operator|.
name|lastGen
operator|=
name|thisFieldGen
expr_stmt|;
block|}
return|return
name|fp
return|;
block|}
DECL|field|fieldsComp
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|DocFieldProcessorPerField
argument_list|>
name|fieldsComp
init|=
operator|new
name|Comparator
argument_list|<
name|DocFieldProcessorPerField
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DocFieldProcessorPerField
name|o1
parameter_list|,
name|DocFieldProcessorPerField
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|storedConsumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|consumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

