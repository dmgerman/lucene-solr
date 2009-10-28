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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
import|;
end_import

begin_comment
comment|/** This is a DocFieldConsumer that inverts each field,  *  separately, from a Document, and accepts a  *  InvertedTermsConsumer to process those terms. */
end_comment

begin_class
DECL|class|DocInverterPerThread
specifier|final
class|class
name|DocInverterPerThread
extends|extends
name|DocFieldConsumerPerThread
block|{
DECL|field|docInverter
specifier|final
name|DocInverter
name|docInverter
decl_stmt|;
DECL|field|consumer
specifier|final
name|InvertedDocConsumerPerThread
name|consumer
decl_stmt|;
DECL|field|endConsumer
specifier|final
name|InvertedDocEndConsumerPerThread
name|endConsumer
decl_stmt|;
comment|//TODO: change to SingleTokenTokenStream after Token was removed
DECL|field|singleTokenTokenStream
specifier|final
name|SingleTokenTokenStream
name|singleTokenTokenStream
init|=
operator|new
name|SingleTokenTokenStream
argument_list|()
decl_stmt|;
DECL|class|SingleTokenTokenStream
specifier|static
class|class
name|SingleTokenTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAttribute
name|TermAttribute
name|termAttribute
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
DECL|method|SingleTokenTokenStream
name|SingleTokenTokenStream
parameter_list|()
block|{
name|termAttribute
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|String
name|stringValue
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|termAttribute
operator|.
name|setTermBuffer
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
comment|// this is a dummy, to not throw an UOE because this class does not implement any iteration method
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
init|=
operator|new
name|FieldInvertState
argument_list|()
decl_stmt|;
comment|// Used to read a string value for a field
DECL|field|stringReader
specifier|final
name|ReusableStringReader
name|stringReader
init|=
operator|new
name|ReusableStringReader
argument_list|()
decl_stmt|;
DECL|method|DocInverterPerThread
specifier|public
name|DocInverterPerThread
parameter_list|(
name|DocFieldProcessorPerThread
name|docFieldProcessorPerThread
parameter_list|,
name|DocInverter
name|docInverter
parameter_list|)
block|{
name|this
operator|.
name|docInverter
operator|=
name|docInverter
expr_stmt|;
name|docState
operator|=
name|docFieldProcessorPerThread
operator|.
name|docState
expr_stmt|;
name|consumer
operator|=
name|docInverter
operator|.
name|consumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|endConsumer
operator|=
name|docInverter
operator|.
name|endConsumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
specifier|public
name|DocumentsWriter
operator|.
name|DocWriter
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: allow endConsumer.finishDocument to also return
comment|// a DocWriter
name|endConsumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
return|return
name|consumer
operator|.
name|finishDocument
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|endConsumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|DocFieldConsumerPerField
name|addField
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
return|return
operator|new
name|DocInverterPerField
argument_list|(
name|this
argument_list|,
name|fi
argument_list|)
return|;
block|}
block|}
end_class

end_unit

