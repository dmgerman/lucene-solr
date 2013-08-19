begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.temp
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
operator|.
name|TempBlockTreeTermsReader
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
name|temp
operator|.
name|TempBlockTreeTermsWriter
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
name|TempPostingsBaseFormat
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
name|TempPostingsReaderBase
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
name|TempPostingsWriterBase
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/** This postings format "inlines" the postings for terms that have  *  low docFreq.  It wraps another postings format, which is used for  *  writing the non-inlined terms.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|TempPulsingPostingsFormat
specifier|public
specifier|abstract
class|class
name|TempPulsingPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|freqCutoff
specifier|private
specifier|final
name|int
name|freqCutoff
decl_stmt|;
DECL|field|minBlockSize
specifier|private
specifier|final
name|int
name|minBlockSize
decl_stmt|;
DECL|field|maxBlockSize
specifier|private
specifier|final
name|int
name|maxBlockSize
decl_stmt|;
DECL|field|wrappedPostingsBaseFormat
specifier|private
specifier|final
name|TempPostingsBaseFormat
name|wrappedPostingsBaseFormat
decl_stmt|;
DECL|method|TempPulsingPostingsFormat
specifier|public
name|TempPulsingPostingsFormat
parameter_list|(
name|String
name|name
parameter_list|,
name|TempPostingsBaseFormat
name|wrappedPostingsBaseFormat
parameter_list|,
name|int
name|freqCutoff
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|wrappedPostingsBaseFormat
argument_list|,
name|freqCutoff
argument_list|,
name|TempBlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|TempBlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** Terms with freq<= freqCutoff are inlined into terms    *  dict. */
DECL|method|TempPulsingPostingsFormat
specifier|public
name|TempPulsingPostingsFormat
parameter_list|(
name|String
name|name
parameter_list|,
name|TempPostingsBaseFormat
name|wrappedPostingsBaseFormat
parameter_list|,
name|int
name|freqCutoff
parameter_list|,
name|int
name|minBlockSize
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|freqCutoff
operator|=
name|freqCutoff
expr_stmt|;
name|this
operator|.
name|minBlockSize
operator|=
name|minBlockSize
expr_stmt|;
assert|assert
name|minBlockSize
operator|>
literal|1
assert|;
name|this
operator|.
name|maxBlockSize
operator|=
name|maxBlockSize
expr_stmt|;
name|this
operator|.
name|wrappedPostingsBaseFormat
operator|=
name|wrappedPostingsBaseFormat
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
name|getName
argument_list|()
operator|+
literal|"(freqCutoff="
operator|+
name|freqCutoff
operator|+
literal|" minBlockSize="
operator|+
name|minBlockSize
operator|+
literal|" maxBlockSize="
operator|+
name|maxBlockSize
operator|+
literal|")"
return|;
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
name|TempPostingsWriterBase
name|docsWriter
init|=
literal|null
decl_stmt|;
comment|// Terms that have<= freqCutoff number of docs are
comment|// "pulsed" (inlined):
name|TempPostingsWriterBase
name|pulsingWriter
init|=
literal|null
decl_stmt|;
comment|// Terms dict
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|docsWriter
operator|=
name|wrappedPostingsBaseFormat
operator|.
name|postingsWriterBase
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|// Terms that have<= freqCutoff number of docs are
comment|// "pulsed" (inlined):
name|pulsingWriter
operator|=
operator|new
name|TempPulsingPostingsWriter
argument_list|(
name|state
argument_list|,
name|freqCutoff
argument_list|,
name|docsWriter
argument_list|)
expr_stmt|;
name|FieldsConsumer
name|ret
init|=
operator|new
name|TempBlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|pulsingWriter
argument_list|,
name|minBlockSize
argument_list|,
name|maxBlockSize
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|docsWriter
argument_list|,
name|pulsingWriter
argument_list|)
expr_stmt|;
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
name|TempPostingsReaderBase
name|docsReader
init|=
literal|null
decl_stmt|;
name|TempPostingsReaderBase
name|pulsingReader
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
name|docsReader
operator|=
name|wrappedPostingsBaseFormat
operator|.
name|postingsReaderBase
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|pulsingReader
operator|=
operator|new
name|TempPulsingPostingsReader
argument_list|(
name|state
argument_list|,
name|docsReader
argument_list|)
expr_stmt|;
name|FieldsProducer
name|ret
init|=
operator|new
name|TempBlockTreeTermsReader
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|pulsingReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|docsReader
argument_list|,
name|pulsingReader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFreqCutoff
specifier|public
name|int
name|getFreqCutoff
parameter_list|()
block|{
return|return
name|freqCutoff
return|;
block|}
block|}
end_class

end_unit

