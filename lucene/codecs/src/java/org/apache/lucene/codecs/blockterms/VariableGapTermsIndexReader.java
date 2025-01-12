begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.blockterms
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blockterms
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
name|fst
operator|.
name|BytesRefFSTEnum
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
name|PositiveIntOutputs
import|;
end_import

begin_comment
comment|/** See {@link VariableGapTermsIndexWriter}  *   * @lucene.experimental */
end_comment

begin_class
DECL|class|VariableGapTermsIndexReader
specifier|public
class|class
name|VariableGapTermsIndexReader
extends|extends
name|TermsIndexReaderBase
block|{
DECL|field|fstOutputs
specifier|private
specifier|final
name|PositiveIntOutputs
name|fstOutputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
DECL|field|fields
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldIndexData
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|VariableGapTermsIndexReader
specifier|public
name|VariableGapTermsIndexReader
parameter_list|(
name|SegmentReadState
name|state
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
name|VariableGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|IndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
operator|new
name|IOContext
argument_list|(
name|state
operator|.
name|context
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|in
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|CODEC_NAME
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
name|VERSION_START
argument_list|,
name|VariableGapTermsIndexWriter
operator|.
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
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|seekDir
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Read directory
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numFields
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid numFields: "
operator|+
name|numFields
argument_list|,
name|in
argument_list|)
throw|;
block|}
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
specifier|final
name|int
name|field
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|indexStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
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
name|FieldIndexData
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
operator|new
name|FieldIndexData
argument_list|(
name|in
argument_list|,
name|fieldInfo
argument_list|,
name|indexStart
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"duplicate field: "
operator|+
name|fieldInfo
operator|.
name|name
argument_list|,
name|in
argument_list|)
throw|;
block|}
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|IndexEnum
specifier|private
specifier|static
class|class
name|IndexEnum
extends|extends
name|FieldIndexEnum
block|{
DECL|field|fstEnum
specifier|private
specifier|final
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
decl_stmt|;
DECL|field|current
specifier|private
name|BytesRefFSTEnum
operator|.
name|InputOutput
argument_list|<
name|Long
argument_list|>
name|current
decl_stmt|;
DECL|method|IndexEnum
specifier|public
name|IndexEnum
parameter_list|(
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
parameter_list|)
block|{
name|fstEnum
operator|=
operator|new
name|BytesRefFSTEnum
argument_list|<>
argument_list|(
name|fst
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
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|current
operator|.
name|input
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|BytesRef
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("VGR: seek field=" + fieldInfo.name + " target=" + target);
name|current
operator|=
name|fstEnum
operator|.
name|seekFloor
argument_list|(
name|target
argument_list|)
expr_stmt|;
comment|//System.out.println("  got input=" + current.input + " output=" + current.output);
return|return
name|current
operator|.
name|output
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("VGR: next field=" + fieldInfo.name);
name|current
operator|=
name|fstEnum
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("  eof");
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|current
operator|.
name|output
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|supportsOrd
specifier|public
name|boolean
name|supportsOrd
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|class|FieldIndexData
specifier|private
specifier|final
class|class
name|FieldIndexData
implements|implements
name|Accountable
block|{
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
DECL|method|FieldIndexData
specifier|public
name|FieldIndexData
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|indexStart
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|clone
init|=
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
name|indexStart
argument_list|)
expr_stmt|;
name|fst
operator|=
operator|new
name|FST
argument_list|<>
argument_list|(
name|clone
argument_list|,
name|fstOutputs
argument_list|)
expr_stmt|;
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
comment|/*       final String dotFileName = segment + "_" + fieldInfo.name + ".dot";       Writer w = new OutputStreamWriter(new FileOutputStream(dotFileName));       Util.toDot(fst, w, false, false);       System.out.println("FST INDEX: SAVED to " + dotFileName);       w.close();       */
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|fst
operator|==
literal|null
condition|?
literal|0
else|:
name|fst
operator|.
name|ramBytesUsed
argument_list|()
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
if|if
condition|(
name|fst
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"index data"
argument_list|,
name|fst
argument_list|)
argument_list|)
return|;
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
literal|"VarGapTermIndex"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldEnum
specifier|public
name|FieldIndexEnum
name|getFieldEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
specifier|final
name|FieldIndexData
name|fieldData
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|.
name|fst
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|IndexEnum
argument_list|(
name|fieldData
operator|.
name|fst
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
block|{}
DECL|method|seekDir
specifier|private
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
name|CodecUtil
operator|.
name|footerLength
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
name|long
name|dirOffset
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
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
literal|0
decl_stmt|;
for|for
control|(
name|FieldIndexData
name|entry
range|:
name|fields
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|entry
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
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"field"
argument_list|,
name|fields
argument_list|)
return|;
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
name|fields
operator|.
name|size
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

