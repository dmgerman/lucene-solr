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
name|util
operator|.
name|ByteBlockPool
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
name|IntBlockPool
import|;
end_import

begin_comment
comment|/** This class is passed each token produced by the analyzer  *  on each field during indexing, and it stores these  *  tokens in a hash table, and allocates separate byte  *  streams per token.  Consumers of this class, eg {@link  *  FreqProxTermsWriter} and {@link TermVectorsConsumer},  *  write their own byte streams under each term. */
end_comment

begin_class
DECL|class|TermsHash
specifier|abstract
class|class
name|TermsHash
block|{
DECL|field|nextTermsHash
specifier|final
name|TermsHash
name|nextTermsHash
decl_stmt|;
DECL|field|intPool
specifier|final
name|IntBlockPool
name|intPool
decl_stmt|;
DECL|field|bytePool
specifier|final
name|ByteBlockPool
name|bytePool
decl_stmt|;
DECL|field|termBytePool
name|ByteBlockPool
name|termBytePool
decl_stmt|;
DECL|field|bytesUsed
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|trackAllocations
specifier|final
name|boolean
name|trackAllocations
decl_stmt|;
DECL|method|TermsHash
name|TermsHash
parameter_list|(
specifier|final
name|DocumentsWriterPerThread
name|docWriter
parameter_list|,
name|boolean
name|trackAllocations
parameter_list|,
name|TermsHash
name|nextTermsHash
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
name|trackAllocations
operator|=
name|trackAllocations
expr_stmt|;
name|this
operator|.
name|nextTermsHash
operator|=
name|nextTermsHash
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|trackAllocations
condition|?
name|docWriter
operator|.
name|bytesUsed
else|:
name|Counter
operator|.
name|newCounter
argument_list|()
expr_stmt|;
name|intPool
operator|=
operator|new
name|IntBlockPool
argument_list|(
name|docWriter
operator|.
name|intBlockAllocator
argument_list|)
expr_stmt|;
name|bytePool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
name|docWriter
operator|.
name|byteBlockAllocator
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
comment|// We are primary
name|termBytePool
operator|=
name|bytePool
expr_stmt|;
name|nextTermsHash
operator|.
name|termBytePool
operator|=
name|bytePool
expr_stmt|;
block|}
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Clear all state
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
comment|// we don't reuse so we drop everything and don't fill with 0
name|intPool
operator|.
name|reset
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bytePool
operator|.
name|reset
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|TermsHashPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TermsHashPerField
argument_list|>
name|nextChildFields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|TermsHashPerField
argument_list|>
name|entry
range|:
name|fieldsToFlush
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|nextChildFields
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|nextPerField
argument_list|)
expr_stmt|;
block|}
name|nextTermsHash
operator|.
name|flush
argument_list|(
name|nextChildFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addField
specifier|abstract
name|TermsHashPerField
name|addField
parameter_list|(
name|FieldInvertState
name|fieldInvertState
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
function_decl|;
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

