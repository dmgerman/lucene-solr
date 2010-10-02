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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|index
operator|.
name|FreqProxTermsWriterPerField
operator|.
name|FreqProxPostingsArray
import|;
end_import

begin_comment
comment|// TODO FI: some of this is "generic" to TermsHash* so we
end_comment

begin_comment
comment|// should factor it out so other consumers don't have to
end_comment

begin_comment
comment|// duplicate this code
end_comment

begin_comment
comment|/** Used by DocumentsWriter to merge the postings from  *  multiple ThreadStates when creating a segment */
end_comment

begin_class
DECL|class|FreqProxFieldMergeState
specifier|final
class|class
name|FreqProxFieldMergeState
block|{
DECL|field|field
specifier|final
name|FreqProxTermsWriterPerField
name|field
decl_stmt|;
DECL|field|numPostings
specifier|final
name|int
name|numPostings
decl_stmt|;
DECL|field|bytePool
specifier|private
specifier|final
name|ByteBlockPool
name|bytePool
decl_stmt|;
DECL|field|termIDs
specifier|final
name|int
index|[]
name|termIDs
decl_stmt|;
DECL|field|postings
specifier|final
name|FreqProxPostingsArray
name|postings
decl_stmt|;
DECL|field|currentTermID
name|int
name|currentTermID
decl_stmt|;
DECL|field|text
specifier|final
name|BytesRef
name|text
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|postingUpto
specifier|private
name|int
name|postingUpto
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|freq
specifier|final
name|ByteSliceReader
name|freq
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
DECL|field|prox
specifier|final
name|ByteSliceReader
name|prox
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|termFreq
name|int
name|termFreq
decl_stmt|;
DECL|method|FreqProxFieldMergeState
specifier|public
name|FreqProxFieldMergeState
parameter_list|(
name|FreqProxTermsWriterPerField
name|field
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|numPostings
operator|=
name|field
operator|.
name|termsHashPerField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
expr_stmt|;
name|this
operator|.
name|bytePool
operator|=
name|field
operator|.
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|bytePool
expr_stmt|;
name|this
operator|.
name|termIDs
operator|=
name|field
operator|.
name|termsHashPerField
operator|.
name|sortPostings
argument_list|(
name|termComp
argument_list|)
expr_stmt|;
name|this
operator|.
name|postings
operator|=
operator|(
name|FreqProxPostingsArray
operator|)
name|field
operator|.
name|termsHashPerField
operator|.
name|postingsArray
expr_stmt|;
block|}
DECL|method|nextTerm
name|boolean
name|nextTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|postingUpto
operator|++
expr_stmt|;
if|if
condition|(
name|postingUpto
operator|==
name|numPostings
condition|)
block|{
return|return
literal|false
return|;
block|}
name|currentTermID
operator|=
name|termIDs
index|[
name|postingUpto
index|]
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
comment|// Get BytesRef
specifier|final
name|int
name|textStart
init|=
name|postings
operator|.
name|textStarts
index|[
name|currentTermID
index|]
decl_stmt|;
name|bytePool
operator|.
name|setBytesRef
argument_list|(
name|text
argument_list|,
name|textStart
argument_list|)
expr_stmt|;
name|field
operator|.
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|freq
argument_list|,
name|currentTermID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
condition|)
block|{
name|field
operator|.
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|prox
argument_list|,
name|currentTermID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Should always be true
name|boolean
name|result
init|=
name|nextDoc
argument_list|()
decl_stmt|;
assert|assert
name|result
assert|;
return|return
literal|true
return|;
block|}
DECL|method|nextDoc
specifier|public
name|boolean
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|freq
operator|.
name|eof
argument_list|()
condition|)
block|{
if|if
condition|(
name|postings
operator|.
name|lastDocCodes
index|[
name|currentTermID
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Return last doc
name|docID
operator|=
name|postings
operator|.
name|lastDocIDs
index|[
name|currentTermID
index|]
expr_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|omitTermFreqAndPositions
condition|)
name|termFreq
operator|=
name|postings
operator|.
name|docFreqs
index|[
name|currentTermID
index|]
expr_stmt|;
name|postings
operator|.
name|lastDocCodes
index|[
name|currentTermID
index|]
operator|=
operator|-
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
comment|// EOF
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|code
init|=
name|freq
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|omitTermFreqAndPositions
condition|)
name|docID
operator|+=
name|code
expr_stmt|;
else|else
block|{
name|docID
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
name|termFreq
operator|=
literal|1
expr_stmt|;
else|else
name|termFreq
operator|=
name|freq
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
assert|assert
name|docID
operator|!=
name|postings
operator|.
name|lastDocIDs
index|[
name|currentTermID
index|]
assert|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

