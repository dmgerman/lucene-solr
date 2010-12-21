begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.mockintblock
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|mockintblock
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
name|Set
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
name|SegmentInfo
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
name|index
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
name|index
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
name|index
operator|.
name|codecs
operator|.
name|sep
operator|.
name|IntStreamFactory
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
name|codecs
operator|.
name|sep
operator|.
name|IntIndexInput
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
name|codecs
operator|.
name|sep
operator|.
name|IntIndexOutput
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
name|codecs
operator|.
name|sep
operator|.
name|SepPostingsReaderImpl
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
name|codecs
operator|.
name|sep
operator|.
name|SepPostingsWriterImpl
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
name|codecs
operator|.
name|intblock
operator|.
name|VariableIntBlockIndexInput
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
name|codecs
operator|.
name|intblock
operator|.
name|VariableIntBlockIndexOutput
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
name|codecs
operator|.
name|FixedGapTermsIndexReader
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
name|codecs
operator|.
name|FixedGapTermsIndexWriter
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
name|codecs
operator|.
name|PostingsReaderBase
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
name|codecs
operator|.
name|PrefixCodedTermsReader
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
name|codecs
operator|.
name|PrefixCodedTermsWriter
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
name|codecs
operator|.
name|TermsIndexReaderBase
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
name|codecs
operator|.
name|TermsIndexWriterBase
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
name|codecs
operator|.
name|standard
operator|.
name|StandardCodec
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A silly test codec to verify core support for variable  * sized int block encoders is working.  The int encoder  * used here writes baseBlockSize ints at once, if the first  * int is<= 3, else 2*baseBlockSize.  */
end_comment

begin_class
DECL|class|MockVariableIntBlockCodec
specifier|public
class|class
name|MockVariableIntBlockCodec
extends|extends
name|Codec
block|{
DECL|field|baseBlockSize
specifier|private
specifier|final
name|int
name|baseBlockSize
decl_stmt|;
DECL|method|MockVariableIntBlockCodec
specifier|public
name|MockVariableIntBlockCodec
parameter_list|(
name|int
name|baseBlockSize
parameter_list|)
block|{
name|name
operator|=
literal|"MockVariableIntBlock"
expr_stmt|;
name|this
operator|.
name|baseBlockSize
operator|=
name|baseBlockSize
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
name|name
operator|+
literal|"(baseBlockSize="
operator|+
name|baseBlockSize
operator|+
literal|")"
return|;
block|}
DECL|class|MockIntFactory
specifier|private
class|class
name|MockIntFactory
extends|extends
name|IntStreamFactory
block|{
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IntIndexInput
name|openInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|readBufferSize
argument_list|)
decl_stmt|;
specifier|final
name|int
name|baseBlockSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
return|return
operator|new
name|VariableIntBlockIndexInput
argument_list|(
name|in
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BlockReader
name|getBlockReader
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|,
specifier|final
name|int
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BlockReader
argument_list|()
block|{
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
block|{}
specifier|public
name|int
name|readBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|buffer
index|[
literal|0
index|]
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|buffer
index|[
literal|0
index|]
operator|<=
literal|3
condition|?
name|baseBlockSize
operator|-
literal|1
else|:
literal|2
operator|*
name|baseBlockSize
operator|-
literal|1
decl_stmt|;
assert|assert
name|buffer
operator|.
name|length
operator|>=
name|count
operator|:
literal|"buffer.length="
operator|+
name|buffer
operator|.
name|length
operator|+
literal|" count="
operator|+
name|count
assert|;
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
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
return|return
literal|1
operator|+
name|count
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IntIndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|baseBlockSize
argument_list|)
expr_stmt|;
return|return
operator|new
name|VariableIntBlockIndexOutput
argument_list|(
name|out
argument_list|,
literal|2
operator|*
name|baseBlockSize
argument_list|)
block|{
name|int
name|pendingCount
decl_stmt|;
specifier|final
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|2
operator|+
literal|2
operator|*
name|baseBlockSize
index|]
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|int
name|add
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
index|[
name|pendingCount
operator|++
index|]
operator|=
name|value
expr_stmt|;
comment|// silly variable block length int encoder: if
comment|// first value<= 3, we write N vints at once;
comment|// else, 2*N
specifier|final
name|int
name|flushAt
init|=
name|buffer
index|[
literal|0
index|]
operator|<=
literal|3
condition|?
name|baseBlockSize
else|:
literal|2
operator|*
name|baseBlockSize
decl_stmt|;
comment|// intentionally be non-causal here:
if|if
condition|(
name|pendingCount
operator|==
name|flushAt
operator|+
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|flushAt
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buffer
index|[
literal|0
index|]
operator|=
name|buffer
index|[
name|flushAt
index|]
expr_stmt|;
name|pendingCount
operator|=
literal|1
expr_stmt|;
return|return
name|flushAt
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
return|;
block|}
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
name|PostingsWriterBase
name|postingsWriter
init|=
operator|new
name|SepPostingsWriterImpl
argument_list|(
name|state
argument_list|,
operator|new
name|MockIntFactory
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|TermsIndexWriterBase
name|indexWriter
decl_stmt|;
try|try
block|{
name|indexWriter
operator|=
operator|new
name|FixedGapTermsIndexWriter
argument_list|(
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
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|PrefixCodedTermsWriter
argument_list|(
name|indexWriter
argument_list|,
name|state
argument_list|,
name|postingsWriter
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|PostingsReaderBase
name|postingsReader
init|=
operator|new
name|SepPostingsReaderImpl
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
operator|new
name|MockIntFactory
argument_list|()
argument_list|,
name|state
operator|.
name|codecId
argument_list|)
decl_stmt|;
name|TermsIndexReaderBase
name|indexReader
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|indexReader
operator|=
operator|new
name|FixedGapTermsIndexReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|,
name|state
operator|.
name|codecId
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
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|PrefixCodedTermsReader
argument_list|(
name|indexReader
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|postingsReader
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|,
name|StandardCodec
operator|.
name|TERMS_CACHE_SIZE
argument_list|,
name|state
operator|.
name|codecId
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|codecId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|SepPostingsReaderImpl
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|codecId
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|PrefixCodedTermsReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|codecId
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|FixedGapTermsIndexReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|codecId
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|SepPostingsWriterImpl
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|PrefixCodedTermsReader
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|FixedGapTermsIndexReader
operator|.
name|getIndexExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

